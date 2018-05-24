package dao

import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.db.Database
import play.api.db.evolutions.{ Evolutions, SimpleEvolutionsReader }
import play.api.inject.guice.GuiceApplicationBuilder

abstract class DatabaseTest extends PlaySpec with GuiceOneAppPerSuite with BeforeAndAfter {

  implicit override lazy val app: Application = new GuiceApplicationBuilder()
    .configure("db.default.url" -> sys.env.getOrElse("DB_TEST_URL", "jdbc:sqlite:db/tests.sqlite"))
    .build

  before {
    val db = app.injector.instanceOf[Database]

    // Load the database schema
    Evolutions.applyEvolutions(db)

    // Insert test data
    Evolutions.applyEvolutions(db, SimpleEvolutionsReader.forDefault())
  }

  after {
    val db = app.injector.instanceOf[Database]
    Evolutions.cleanupEvolutions(db)
  }

}
