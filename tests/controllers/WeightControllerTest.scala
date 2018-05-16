package controllers

import akka.actor.ActorSystem
import akka.stream.{ ActorMaterializer, Materializer }
import dao.WeightPointDAO
import model.WeightPoint
import org.scalatestplus.play.PlaySpec
import org.scalatest.mockito.MockitoSugar
import play.api.test._
import org.mockito.Mockito._
import play.api.test.Helpers._

import scala.util.{ Failure, Success }

class WeightControllerTest extends PlaySpec with MockitoSugar {

  private val mockDao = mock[WeightPointDAO]
  private val weightController = new WeightController(mockDao, stubControllerComponents())
  private val dummyDate = "2018-01-02 03:04:05"
  private val dummyWeight = WeightPoint.applyRaw(dummyDate, 6.0, 7.0, 8.0, 9.0)

  // Not sure why I need these
  implicit val system: ActorSystem = ActorSystem("Test")
  implicit def mat: Materializer = ActorMaterializer()

  "getAll" must {

    val request = FakeRequest()

    "return Ok when the DAO returns Success" in {
      when(mockDao.fetchAll(None, None)) thenReturn Success(Nil)
      val result = weightController.getAll(None, None).apply(request)
      status(result) mustBe OK
    }

    "return INTERNAL_SERVER_ERROR when the DAO returns Failure" in {
      when(mockDao.fetchAll(None, None)) thenReturn Failure(new Exception())
      val result = weightController.getAll(None, None).apply(request)
      status(result) mustBe INTERNAL_SERVER_ERROR
    }

  }

  "getWeekly" must {

    val request = FakeRequest()

    "return Ok when the DAO returns Success" in {
      when(mockDao.fetchWeekly(None, None)) thenReturn Success(Nil)
      val result = weightController.getWeekly(None, None).apply(request)
      status(result) mustBe OK
    }

    "return Failure when the DAO returns Failure" in {
      when(mockDao.fetchWeekly(None, None)) thenReturn Failure(new Exception())
      val result = weightController.getWeekly(None, None).apply(request)
      status(result) mustBe INTERNAL_SERVER_ERROR
    }

  }

  "add" must {

    val request = FakeRequest().withMethod(PUT).withBody(dummyWeight.toJson)

    "return CREATED when the DAO returns Success" in {
      when(mockDao.insert(dummyWeight)) thenReturn Success {}
      val result = weightController.add().apply(request)
      status(result) mustBe CREATED
    }

    "return Failure when the DAO returns Failure" in {
      when(mockDao.insert(dummyWeight)) thenReturn Failure(new Exception())
      val result = weightController.add().apply(request)
      status(result) mustBe INTERNAL_SERVER_ERROR
    }

  }

  "modify" must {

    val request = FakeRequest().withMethod(POST).withBody(dummyWeight.toJson)

    "return Ok when the DAO returns Success" in {
      when(mockDao.update(dummyWeight)) thenReturn Success {}
      val result = weightController.modify(dummyDate).apply(request)
      status(result) mustBe ACCEPTED
    }

    "return Failure when the DAO returns Failure" in {
      when(mockDao.update(dummyWeight)) thenReturn Failure(new Exception())
      val result = weightController.modify(dummyDate).apply(request)
      status(result) mustBe INTERNAL_SERVER_ERROR
    }

  }

  "remove" must {

    val request = FakeRequest().withMethod(DELETE)
    "return Ok when the DAO returns Success" in {
      when(mockDao.delete(dummyDate)) thenReturn Success {}
      val result = weightController.remove(dummyDate).apply(request)
      status(result) mustBe OK
    }

    "return Failure when the DAO returns Failure" in {
      when(mockDao.delete(dummyDate)) thenReturn Failure(new Exception())
      val result = weightController.remove(dummyDate).apply(request)
      status(result) mustBe INTERNAL_SERVER_ERROR
    }

  }

}
