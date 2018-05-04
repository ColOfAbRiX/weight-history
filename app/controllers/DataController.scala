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
  private def successResponse(data: JsValue = Json.obj()): JsValue =
    Json.obj("result" -> "success", "data" -> data)

  /**
    * Builds a response for a failed action
    */
  private def failedResponse(description: String): JsValue =
    Json.obj("result" -> "fail", "message" -> description)

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
    * API Endpoint
    */
  def getAll(start: Option[String], end: Option[String]) = Action {
    new WeightPoint.DataAccess(db)
      .fetchAll(start, end) match {
        case TrySuccess(weights) =>
          Ok(successResponse(WeightPoint.toJson(weights)))
        case TryFailure(e) =>
          InternalServerError(failedResponse(e.getMessage))
      }
  }

  /**
    * API Endpoint
    */
  def getWeekly(start: Option[String], end: Option[String]) = Action {
    new WeightPoint.DataAccess(db)
      .fetchWeekly(start, end) match {
        case TrySuccess(weights) =>
          Ok(successResponse(WeightPoint.toJson(weights)))
        case TryFailure(e) =>
          InternalServerError(failedResponse(e.getMessage))
    }
  }

  /**
    * API Endpoint
    */
  def add() = Action(parse.json) { request =>
    withJsonValidation(request) { weight =>
      new WeightPoint.DataAccess(db).insert(weight) match {
        case TrySuccess(_) =>
          Ok(successResponse())
        case TryFailure(e) =>
          InternalServerError(failedResponse(e.getMessage))
      }
    }
  }

  /**
    * API Endpoint
    */
  def modify(date: String) = Action(parse.json) { request =>
    withJsonValidation(request) { weight =>
      new WeightPoint.DataAccess(db).update(weight) match {
        case TrySuccess(_) =>
          Ok(successResponse())
        case TryFailure(e) =>
          InternalServerError(failedResponse(e.getMessage))
      }
    }
  }

  /**
    * API Endpoint
    */
  def remove(date: String) = Action {
    new WeightPoint.DataAccess(db).delete(date) match {
      case TrySuccess(_) =>
        Ok(successResponse())
      case TryFailure(e) =>
        InternalServerError(failedResponse(e.getMessage))
    }
  }
}
