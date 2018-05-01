package controllers

import anorm._
import javax.inject._
import model.WeightPoint._
import model._
import play.api.db._
import play.api.libs.json._
import play.api.mvc._

@Singleton
class DataController @Inject()(db: Database, cc: ControllerComponents)
  extends AbstractController(cc)  {

  def getAll(start: Option[Long], end: Option[Long]) = Action {
    db.withConnection { implicit conn: java.sql.Connection =>
      val points = SQL("SELECT * FROM weights;").as(WeightPoint.anormParser.*)
      Ok(Json.toJson(points))
    }
  }

  def getWeekly(start: Option[Long], end: Option[Long]) = Action {
    Ok("getWeekly")
  }

  def add() = Action(parse.json) { request =>
      Ok(s"Add ${request.body}")
  }

  def modify(id: Long) = Action(parse.json) { request =>
    Ok(s"Modify ${request.body}")
  }

  def remove(id: Long) = Action {
    Ok(s"remove $id")
  }

}
