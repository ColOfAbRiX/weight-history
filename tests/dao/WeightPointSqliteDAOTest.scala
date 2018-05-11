package dao

import java.sql.Connection

import anorm.SqlParser.get
import anorm.{ NamedParameter, RowParser, SQL, ~ }
import javax.inject.Inject
import model.WeightPoint
import org.scalatestplus.play.PlaySpec
import play.api.db.Database

import scala.util.Try

class WeightPointSqliteDAOTest extends PlaySpec {

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

    "insert a new weight when the weight is not present" in {}

    "not insert a weight when the weight is already present" in {}

    "return a failure when there is a problem in the DB" in {}

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
