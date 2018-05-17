package controllers

import dao.WeightPointDAO
import javax.inject._
import model.WeightPoint
import play.api.libs.json._
import play.api.mvc._
import play.api.Logger

import scala.util.{ Failure, Success }

@Singleton
class WeightController @Inject()(wd: WeightPointDAO, cc: ControllerComponents)
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
        Logger.error(s"Invalid JSON for WeightPoint: ${error.toString}")
        BadRequest( failedResponse( error.toString() ) )
    }

  /**
    * API Endpoint
    */
  def getAll(start: Option[String], end: Option[String]) = Action {
    wd.fetchAll(start, end) match {
      case Success(weights) =>
        Ok(successResponse(WeightPoint.toJson(weights)))
      case Failure(e) =>
        Logger.error(s"Error while fetchAll($start, $end): ${e.getMessage}")
        InternalServerError(failedResponse(e.getMessage))
    }
  }

  /**
    * API Endpoint
    */
  def getWeekly(start: Option[String], end: Option[String]) = Action {
    wd.fetchWeekly(start, end) match {
      case Success(weights) =>
        Ok(successResponse(WeightPoint.toJson(weights)))
      case Failure(e) =>
        Logger.error(s"Error while getWeekly($start, $end): ${e.getMessage}")
        InternalServerError(failedResponse(e.getMessage))
    }
  }

  /**
    * API Endpoint
    */
  def add() = Action(parse.json) { request =>
    withJsonValidation(request) { weight =>
      wd.insert(weight) match {
        case Success(_) =>
          Created(successResponse())
        case Failure(e) =>
          Logger.error(s"Error while add(): ${e.getMessage}")
          InternalServerError(failedResponse(e.getMessage))
      }
    }
  }

  /**
    * API Endpoint
    */
  def modify(date: String) = Action(parse.json) { request =>
    withJsonValidation(request) { weight =>
      wd.update(weight) match {
        case Success(_) =>
          Accepted(successResponse())
        case Failure(e) =>
          Logger.error(s"Error while modify($date): ${e.getMessage}")
          InternalServerError(failedResponse(e.getMessage))
      }
    }
  }

  /**
    * API Endpoint
    */
  def remove(date: String) = Action {
    wd.delete(date) match {
      case Success(_) =>
        Ok(successResponse())
      case Failure(e) =>
        Logger.error(s"Error while remove($date): ${e.getMessage}")
        InternalServerError(failedResponse(e.getMessage))
    }
  }
}
