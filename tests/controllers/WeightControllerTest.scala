package controllers

import akka.actor.ActorSystem
import akka.stream.{ ActorMaterializer, Materializer }
import dao.WeightPointDAO
import model.WeightPoint
import org.scalatestplus.play.PlaySpec
import org.scalatest.mockito.MockitoSugar
import play.api.test._
import org.mockito.Mockito._
import play.api.libs.json.{ JsString, Json }
import play.api.test.Helpers._

import scala.util.{ Failure, Success }

class WeightControllerTest extends PlaySpec with MockitoSugar {

  // Some definitions
  private val mockDao = mock[WeightPointDAO]
  private val weightController = new WeightController(mockDao, stubControllerComponents())
  private val dummyDate = "2018-01-02 03:04:05"
  private val dummyWeight = WeightPoint.applyRaw(dummyDate, 6.0, 7.0, 8.0, 9.0)

  // Predefined request types
  private val emptyRequest = FakeRequest()
  private val goodRequest = FakeRequest()
    .withHeaders(CONTENT_TYPE -> "application/json")
    .withBody(dummyWeight.toJson)
  private val badJsonRequest = FakeRequest()
    .withHeaders(CONTENT_TYPE -> "application/json")
    .withBody(Json.obj())
  private val notJsonRequest = FakeRequest()
    .withHeaders(CONTENT_TYPE -> "text/plain")
    .withBody("junk")

  // Not sure why I need these
  implicit val system: ActorSystem = ActorSystem("Test")
  implicit def mat: Materializer = ActorMaterializer()

  "getAll" must {

    "return OK when the DAO returns Success" in {
      when(mockDao.fetchAll(None, None)) thenReturn Success(Nil)

      val result = weightController
        .getAll(None, None)
        .apply(emptyRequest.withMethod(GET))

      status(result) mustBe OK
      (contentAsJson(result) \ "result").get mustBe JsString("success")
    }

    "return INTERNAL_SERVER_ERROR when the DAO returns Failure" in {
      when(mockDao.fetchAll(None, None)) thenReturn Failure(new Exception())

      val result = weightController
        .getAll(None, None)
        .apply(emptyRequest.withMethod(GET))

      status(result) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "result").get mustBe JsString("fail")
    }

  }

  "getWeekly" must {

    "return OK when the DAO returns Success" in {
      when(mockDao.fetchWeekly(None, None)) thenReturn Success(Nil)

      val result = weightController
        .getWeekly(None, None)
        .apply(emptyRequest.withMethod(GET))

      status(result) mustBe OK
      (contentAsJson(result) \ "result").get mustBe JsString("success")
    }

    "return INTERNAL_SERVER_ERROR when the DAO returns Failure" in {
      when(mockDao.fetchWeekly(None, None)) thenReturn Failure(new Exception())

      val result = weightController
        .getWeekly(None, None)
        .apply(emptyRequest.withMethod(GET))

      status(result) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "result").get mustBe JsString("fail")
    }

    "return UNSUPPORTED_MEDIA_TYPE when not given JSON" in {
      val result = weightController
        .add()
        .apply(notJsonRequest.withMethod(PUT))

      status(result) mustBe UNSUPPORTED_MEDIA_TYPE
    }

  }

  "add" must {

    "return CREATED when the DAO returns Success" in {
      when(mockDao.insert(dummyWeight)) thenReturn Success {}

      val result = weightController
        .add()
        .apply(goodRequest.withMethod(PUT))

      status(result) mustBe CREATED
      (contentAsJson(result) \ "result").get mustBe JsString("success")
    }

    "return INTERNAL_SERVER_ERROR when the DAO returns Failure" in {
      when(mockDao.insert(dummyWeight)) thenReturn Failure(new Exception())

      val result = weightController
        .add()
        .apply(goodRequest.withMethod(PUT))

      status(result) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "result").get mustBe JsString("fail")
    }

    "return BAD_REQUEST when given a malformed JSON" in {
      val result = weightController
        .add()
        .apply(badJsonRequest.withMethod(PUT))

      status(result) mustBe BAD_REQUEST
      (contentAsJson(result) \ "result").get mustBe JsString("fail")
    }

    "return UNSUPPORTED_MEDIA_TYPE when not given JSON" in {
      val result = weightController
        .add()
        .apply(notJsonRequest.withMethod(PUT))

      status(result) mustBe UNSUPPORTED_MEDIA_TYPE
    }

  }

  "modify" must {

    "return Ok when the DAO returns Success" in {
      when(mockDao.update(dummyWeight)) thenReturn Success { 1 }

      val result = weightController
        .modify(dummyDate)
        .apply(goodRequest.withMethod(POST))

      status(result) mustBe ACCEPTED
      (contentAsJson(result) \ "result").get mustBe JsString("success")
    }

    "return INTERNAL_SERVER_ERROR when the DAO returns Failure" in {
      when(mockDao.update(dummyWeight)) thenReturn Failure(new Exception())

      val result = weightController
        .modify(dummyDate)
        .apply(goodRequest.withMethod(POST))

      status(result) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "result").get mustBe JsString("fail")
    }

    "return BAD_REQUEST when given a malformed JSON" in {
      val result = weightController
        .modify(dummyDate)
        .apply(badJsonRequest.withMethod(POST))

      status(result) mustBe BAD_REQUEST
      (contentAsJson(result) \ "result").get mustBe JsString("fail")
    }

    "return UNSUPPORTED_MEDIA_TYPE when not given JSON" in {
      val result = weightController
        .modify(dummyDate)
        .apply(notJsonRequest.withMethod(PUT))

      status(result) mustBe UNSUPPORTED_MEDIA_TYPE
    }

  }

  "remove" must {

    "return Ok when the DAO returns Success" in {
      when(mockDao.delete(dummyDate)) thenReturn Success { 1 }

      val result = weightController
        .remove(dummyDate)
        .apply(goodRequest.withMethod(DELETE))

      status(result) mustBe OK
      (contentAsJson(result) \ "result").get mustBe JsString("success")
    }

    "return INTERNAL_SERVER_ERROR when the DAO returns Failure" in {
      when(mockDao.delete(dummyDate)) thenReturn Failure(new Exception())

      val result = weightController
        .remove(dummyDate)
        .apply(goodRequest.withMethod(DELETE))

      status(result) mustBe INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "result").get mustBe JsString("fail")
    }

  }

}
