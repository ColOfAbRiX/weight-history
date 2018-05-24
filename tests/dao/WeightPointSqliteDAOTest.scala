package dao

import model.WeightPoint
import play.api.db.Database

import scala.util.{ Failure, Success }

class WeightPointSqliteDAOTest extends DatabaseTest {

  private val dao: WeightPointDAO = new WeightPointSqliteDAO(app.injector.instanceOf[Database])
  private val dummyDate = "2018-01-02 03:04:05"
  private val dummyWeight = WeightPoint.applyRaw(dummyDate, 6.0, 7.0, 8.0, 9.0)

  "fetchAll" must {

    "return the weights when no start and end are provided" in {}

    "return the weights when only start is provided" in {}

    "return the weights when only end is provided" in {}

    "return the weights when both start and end are provided" in {}

    "return nothing when no weights are provided" in {}

    "return a failure when a date is malformed" in {}

    "return a failure when there is a problem in the DB" in {}

  }

  "fetchWeekly" must {

    "return the the weekly weight average when no start and end are provided" in {}

    "return the the weekly weight average when only start is provided" in {}

    "return the the weekly weight average when only end is provided" in {}

    "return the the weekly weight average when both start and end are provided" in {}

    "return a failure when a date is malformed" in {}

    "return a failure when there is a problem in the DB" in {}

  }

  "insert" must {

    "insert a new weight when the weight is not present" in {
      // Add the weight
      this.dao.insert(dummyWeight) match {
        case Success(_) =>
          // And we expect to find it only once in the DB
          this.dao.fetchAll(None, None) match {
            case Success(data) =>
              data.count(_ == dummyWeight) mustBe 1
            case Failure(e) => fail(e)
          }
        case Failure(e) => fail(e)
      }
    }

    "not insert a weight when the weight is already present" in {
      // Add the weight once...
      this.dao.insert(dummyWeight) match {
        case Success(_) =>
          // ... and twice, but this time it must fail and not insert it
          this.dao.insert(dummyWeight) match {
            case Success(_) => fail()
            case Failure(_) =>
              // And we expect to find it only once in the DB
              this.dao.fetchAll(None, None) match {
                case Success(data) =>
                  data.count(_ == dummyWeight) mustBe 1
                case Failure(e) => fail(e)
              }

          }
        case Failure(e) => fail(e)
      }
    }

  }

  "update" must {

    "return a failure when the weight is not present" in {}

    "update the weight when the weight is already present" in {}

    "return a failure when there is a problem in the DB" in {}

  }

  "delete" must {

    "return a failure when the weight is not present" in {}

    "delete the weight when the weight is already present" in {}

    "return a failure when there is a problem in the DB" in {}

  }

}
