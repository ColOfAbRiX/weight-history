package controllers

import anorm._
import javax.inject._
import java.sql.Connection

import model.WeightPoint._
import model._
import play.api.db._
import play.api.libs.json._
import play.api.mvc._
import scala.util.Try

@Singleton
class DataController @Inject()(db: Database, cc: ControllerComponents)
  extends AbstractController(cc)  {

  /**
    * Fetches all the weight points and optionally filter them by date
    */
  def getAll(start: Option[String], end: Option[String]) = Action {
    db.withConnection { implicit conn: Connection =>
      // Building the SQL to get all the points
      val stmt = "SELECT * FROM weights" + ((start, end) match {
        case (None, None) => ";"
        case (Some(dStart), None) => s" WHERE measure_date > '$dStart';"
        case (None, Some(dEnd)) => s" WHERE measure_date < '$dEnd';"
        case (Some(dStart), Some(dEnd)) => s" WHERE measure_date BETWEEN '$dStart' AND '$dEnd';"
      })

      // Query the DB and return the value
      val data = SQL(stmt).as(WeightPoint.dbReader.*)
      Ok(Json.toJson(data))
    }
  }

  /**
    * Fetches all the weight points and group them by week
    */
  def getWeekly(start: Option[String], end: Option[String]) = Action {
    Ok("getWeekly")
  }

  /**
    * Add a new weight point
    */
  def add() = Action(parse.json) { request =>

    // Validate the body
    request.body.validate[WeightPoint] match {

      case success: JsSuccess[WeightPoint] =>
        // Build the SQL statement
        val params = WeightPoint.dbWriter(success.get)
        val names = params map { _.name } mkString ", "
        val tokens = params map { p => s"{${p.name}}" } mkString ", "
        val stmt = s"INSERT INTO weights($names) VALUES ($tokens)"

        // Query the DB
        Try {
          db.withConnection { implicit conn: Connection =>
            SQL( stmt ).on( params: _* ).executeInsert()
          }
        }
        match {
          case scala.util.Success(_) =>
            // All good, return success
            Ok(Json.obj("result" -> "success"))

          case scala.util.Failure(e) =>
            // Error in the DB access
            InternalServerError(Json.obj(
              "result" -> "failed",
              "message" -> e.getMessage
            ))
        }

      case JsError(error) =>
        // Error in the JSON format
        BadRequest(Json.obj(
          "result" -> "failed",
          "message" -> JsError.toJson(error)
        ))
    }
  }

  /**
    * Modifies an existing weight point given its date
    */
  def modify(date: String) = Action(parse.json) { request =>
    Ok(s"Modify $date with ${request.body}")
  }

  /**
    * Removes a weight point given its date
    */
  def remove(date: String) = Action {
    Ok(s"remove $date")
  }

}
