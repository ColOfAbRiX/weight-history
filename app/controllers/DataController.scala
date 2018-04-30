package controllers

import javax.inject._
import play.api.mvc._
import model._

@Singleton
class DataController @Inject()(cc: ControllerComponents) extends AbstractController(cc)  {

  def getAll(start: Option[Long], end: Option[Long]) = Action {
    Ok("getAll")
  }

  def getWeekly(start: Option[Long], end: Option[Long]) = Action {
    Ok("getWeekly")
  }

  def add = Action { request =>
    request.body.asJson match {
      case None => BadRequest("Missing JSON content")
      case Some(json) => Ok(s"add: $json")
    }
  }

  def modify(id: Long) = Action { request =>
    request.body.asJson match {
      case None => BadRequest("Missing JSON content")
      case Some(json) => Ok(s"modify: $json")
    }
  }

  def remove(id: Long) = Action {
    Ok("remove")
  }

}
