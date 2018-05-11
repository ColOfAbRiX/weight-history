package controllers

import dao.WeightPointDAO
import javax.inject._
import model.WeightPoint
import org.scalatestplus.play.PlaySpec
import play.api.libs.json._
import play.api.mvc._

import scala.util.{ Failure, Success }

class WeightControllerTest extends PlaySpec {

  "getAll" must {

    "return Ok when called with no parameters" in {}

    "return Ok when called with start date" in {}

    "return Ok when called with end date" in {}

    "return Ok when called with start and end date" in {}

    "return Ok when no data is present" in {}

    "return Failure when called with wrong parameters" in {}

    "return Failure when called with wrong parameters" in {}

    "return Failure when the DAO has a problem" in {}

  }

  "getWeekly" must {

    "return Ok when the DAO returns Success" in {}

    "return Failure when the DAO returns Failure" in {}

  }

  "add" must {

    "return Ok when the DAO returns Success" in {}

    "return Failure when the DAO returns Failure" in {}

  }

  "modify" must {

    "return Ok when the DAO returns Success" in {}

    "return Failure when the DAO returns Failure" in {}

  }

  "remove" must {

    "return Ok when the DAO returns Success" in {}

    "return Failure when the DAO returns Failure" in {}

  }

}
