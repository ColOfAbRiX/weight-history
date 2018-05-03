package controllers

import javax.inject._
import model._
import play.api.db._
import play.api.libs.json._
import play.api.mvc._
import scala.util.{ Success => TrySuccess, Failure => TryFailure }

@Singleton
class DataController @Inject()(db: Database, cc: ControllerComponents)
  extends AbstractController(cc)  {

  /**
    * Builds a response for a succeeded action
    */
  private def successResponse(data: JsValue): JsValue =
    Json.obj(
      "result" -> "success",
      "data" -> data
    )

  /**
    * Builds a response for a failed action
    */
  private def failedResponse(description: String): JsValue =
    Json.obj(
      "result" -> "fail",
      "message" -> description
    )

  /**
    * Performs JSON validation before executing the action
    */
  private def withJsonValidation(request: Request[JsValue])(f: WeightPoint => Result): Result =
    request.body.validate[WeightPoint] match {
      case success: JsSuccess[WeightPoint] =>
        f( success.get )
      case JsError( error ) =>
        BadRequest( failedResponse( error.toString() ) )
    }

  /**
    * ENDPOINT
    */
  def getAll(start: Option[String], end: Option[String]) = Action {

    // Fetch the weights
    new WeightPoint.DataAccess(db)
      .fetchAll(start, end) match {

        // All good, return the weights
        case TrySuccess(weights) =>
          Ok(successResponse(Json.toJson(weights)))

        // Error in the DB access
        case TryFailure(e) =>
          InternalServerError(failedResponse(e.getMessage))

      }

  }

  /**
    * ENDPOINT
    */
  def getWeekly(start: Option[String], end: Option[String]) = Action {

    new WeightPoint.DataAccess(db)
      .fetchWeekly(start, end) match {

        // All good, return the weights
        case TrySuccess(weights) =>
          Ok(successResponse(Json.toJson(weights)))

        // Error in the DB access
        case TryFailure(e) =>
          InternalServerError(failedResponse(e.getMessage))

    }

  }

  /**
    * ENDPOINT
    */
  def add() = Action(parse.json) { request =>
    withJsonValidation(request) { weight =>

      // Add the new weight
      new WeightPoint.DataAccess(db).insert(weight) match {

        // All good, return success
        case TrySuccess(_) =>
          Ok(successResponse(Json.obj()))

        // Error in the DB access
        case TryFailure(e) =>
          InternalServerError(failedResponse(e.getMessage))

      }

    }
  }

  /**
    * ENDPOINT
    */
  def modify(date: String) = Action(parse.json) { request =>
    Ok(s"Modify $date with ${request.body}")
  }

  /**
    * ENDPOINT
    */
  def remove(date: String) = Action {
    Ok(s"remove $date")
  }

}
