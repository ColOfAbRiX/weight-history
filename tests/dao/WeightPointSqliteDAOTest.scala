package dao

import model.WeightPoint
import scala.util.{ Failure, Success }

class WeightPointSqliteDAOTest extends DatabaseTest {

  private val dao: WeightPointDAO = new WeightPointSqliteDAO(db)
  private val dummyDate = "2018-01-02 03:04:05"
  private val dummyWeight = WeightPoint.applyRaw(dummyDate, 6.0, 7.0, 8.0, 9.0)

  /**
    * Performs a fetch on the DB and executes a check
    */
  private def checkWeightInDB(whenSuccess: List[WeightPoint] => Unit): Unit = {
    // And we expect to find it only once in the DB
    this.dao.fetchAll(None, None) match {
      case Success(data) => whenSuccess(data)
      case Failure(e) => fail(e)
    }
  }

  "fetchAll" must {

    "return the weights in the correct range" when {

      "no start and end date are provided" in {
        this.dao.fetchAll( None, None ) match {
          case Success( data ) =>
            data.nonEmpty mustBe true
            data.head.date.toString( WeightPoint.dateFormat ) mustBe "2017-03-01 09:30:00"
            data.last.date.toString( WeightPoint.dateFormat ) mustBe "2018-04-30 08:30:00"
          case Failure( e ) => fail( e )
        }
      }

      "only start date is provided" in {
        this.dao.fetchAll(Some("2017-05-01 09:30:00"), None) match {
          case Success(data) =>
            data.nonEmpty mustBe true
            data.head.date.toString(WeightPoint.dateFormat) mustBe "2017-05-02 09:30:00"
            data.last.date.toString(WeightPoint.dateFormat) mustBe "2018-04-30 08:30:00"
          case Failure(e) => fail(e)
        }
      }

      "only end date is provided" in {
        this.dao.fetchAll(None, Some("2018-02-01 09:30:00")) match {
          case Success(data) =>
            data.nonEmpty mustBe true
            data.head.date.toString(WeightPoint.dateFormat) mustBe "2017-03-01 09:30:00"
            data.last.date.toString(WeightPoint.dateFormat) mustBe "2018-02-01 08:30:00"
          case Failure(e) => fail(e)
        }
      }

      "both start and end date are provided" in {
        this.dao.fetchAll(Some("2017-05-01 09:30:00"), Some("2018-02-01 09:30:00")) match {
          case Success(data) =>
            data.nonEmpty mustBe true
            data.head.date.toString(WeightPoint.dateFormat) mustBe "2017-05-02 09:30:00"
            data.last.date.toString(WeightPoint.dateFormat) mustBe "2018-02-01 08:30:00"
          case Failure(e) => fail(e)
        }
      }

    }

    "return an empty set" when {

      "both dates are out of the set" in {
        this.dao.fetchAll( Some( "2010-01-01 00:00:00" ), Some( "2010-12-31 23:59:59" ) ) match {
          case Success( data ) => data.nonEmpty mustBe false
          case Failure( e ) => fail( e )
        }
      }
    }

  }

  "fetchWeekly" must {

    "return the the weekly weight average" when {

      "no start and end date are provided" in {
        this.dao.fetchWeekly( None, None ) match {
          case Success( data ) =>
            data.size mustBe 58
            data.head mustBe WeightPoint.applyRaw("2017-02-27 00:00:00", 78.95, 0.125, 0.605, 0.434)
            data.last mustBe WeightPoint.applyRaw("2018-04-30 00:00:00", 77.8, 0.126, 0.605, 0.432)
          case Failure( e ) => fail( e )
        }
      }

    }

    "return the the weekly weight average" when {

      "only start date is provided" in {
        this.dao.fetchWeekly(Some("2017-05-01 09:30:00"), None) match {
          case Success( data ) =>
            data.size mustBe 49
            data.head mustBe WeightPoint.applyRaw("2017-05-01 00:00:00", 77.475, 0.117, 0.613, 0.439)
            data.last mustBe WeightPoint.applyRaw("2018-04-30 00:00:00", 77.8, 0.126, 0.605,  0.432)
          case Failure( e ) => fail( e )
        }
      }

    }

    "return the the weekly weight average" when {

      "only end date is provided" in {
        this.dao.fetchWeekly(None, Some("2018-02-01 09:30:00")) match {
          case Success( data ) =>
            data.size mustBe 45
            data.head mustBe WeightPoint.applyRaw("2017-02-27 00:00:00", 78.95, 0.125, 0.605, 0.434)
            data.last mustBe WeightPoint.applyRaw("2018-01-29 00:00:00", 78.167, 0.126, 0.603, 0.437)
          case Failure( e ) => fail( e )
        }
      }

    }

    "return the the weekly weight average" when {

      "both start and end date are provided" in {
        this.dao.fetchWeekly(Some("2017-05-01 09:30:00"), Some("2018-02-01 09:30:00")) match {
          case Success( data ) =>
            data.size mustBe 36
            data.head mustBe WeightPoint.applyRaw("2017-05-01 00:00:00", 77.475, 0.117, 0.613, 0.439)
            data.last mustBe WeightPoint.applyRaw("2018-01-29 00:00:00", 78.167, 0.126, 0.603, 0.437)
          case Failure( e ) => fail( e )
        }
      }

    }

    "return an empty set" when {

      "a date out of the set is provided" in {
        this.dao.fetchAll( Some( "2010-01-01 00:00:00" ), Some( "2010-12-31 23:59:59" ) ) match {
          case Success( data ) => data.nonEmpty mustBe false
          case Failure( e ) => fail( e )
        }
      }

    }

  }

  "insert" must {

    "insert a new weight when the weight is not present" in {
      // Add the weight
      this.dao.insert(dummyWeight) match {
        case Success(_) =>
          // And we expect to find it only once in the DB
          checkWeightInDB { _.count( _ == dummyWeight ) mustBe 1 }
          this.dao.delete(dummyDate)
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
              checkWeightInDB { _.count( _ == dummyWeight ) mustBe 1 }
              this.dao.delete(dummyDate)
          }
        case Failure(e) => fail(e)
      }
    }

  }

  "update" must {

    "update the weight when the weight is already present" in {
      // Dummy weight modified for the DB call
      val dummyWeightModified = dummyWeight.copy(weight = 99.0)

      // Insert one weight
      this.dao.insert(dummyWeight) match {
        case Success(_) =>
          // And then modify it
          this.dao.update(dummyWeightModified) match {
            case Success(count) =>
              // And we expect to find it only once in the DB
              checkWeightInDB { _.count( _ == dummyWeightModified ) mustBe 1 }
              count mustBe 1
              this.dao.delete(dummyDate)
            case Failure(e) => fail(e)
          }
        case Failure(e) => fail(e)
      }
    }

    "return zero when the weight is not present" in {
      // Try to update a weight that doesn't exists
      this.dao.update(dummyWeight) match {
        case Success(count) =>
          checkWeightInDB { _.count( _ == dummyWeight ) mustBe 0 }
          count mustBe 0
          this.dao.delete(dummyDate)
        case Failure(e) => fail(e)
      }
    }

  }

  "delete" must {

    "delete the weight when the weight is present" in {
      // Insert one weight
      this.dao.insert(dummyWeight) match {
        case Success(_) =>
          // Try to delete a weight that doesn't exists
          this.dao.delete(dummyDate) match {
            case Success(count) =>
              checkWeightInDB { _.count( _ == dummyWeight ) mustBe 0 }
              count mustBe 1
            case Failure(e) => fail(e)
          }
        case Failure(e) => fail(e)
      }
    }

    "return zero when the weight is not present" in {
      // Try to delete a weight that doesn't exists
      this.dao.delete(dummyDate) match {
        case Success(count) =>
          checkWeightInDB { _.count( _ == dummyWeight ) mustBe 0 }
          count mustBe 0
        case Failure(e) => fail(e)
      }
    }

  }

}
