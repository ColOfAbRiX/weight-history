package dao

import org.joda.time.DateTime
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec
import play.api.db.Databases
import play.api.db.evolutions.Evolutions

class DatabaseTest extends PlaySpec with BeforeAndAfterAll {
  protected implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)

  // Define the test database
  protected val db = Databases(
    name = "tests",
    driver = "org.sqlite.JDBC",
    url = "jdbc:sqlite:db/tests.sqlite"
  )

  override def beforeAll(): Unit = {
    super.beforeAll()
    Evolutions.applyEvolutions(db)
  }

  override def afterAll(): Unit = {
    super.afterAll()
    Evolutions.cleanupEvolutions(db)
    db.shutdown()
  }
}
