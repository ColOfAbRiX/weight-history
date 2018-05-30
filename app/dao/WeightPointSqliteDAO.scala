package dao

import java.sql.Connection

import anorm.SqlParser.get
import anorm.{ NamedParameter, RowParser, SQL, ~ }
import javax.inject.Inject
import model.WeightPoint
import play.api.db.Database

import scala.util.Try


class WeightPointSqliteDAO @Inject() (db: Database) extends WeightPointDAO {

  private def dbReader: RowParser[WeightPoint] =
    get[String]("measure_date") ~
      get[Double]("weight_kg") ~
      get[Option[Double]]("fat_percent") ~
      get[Option[Double]]("water_percent") ~
      get[Option[Double]]("muscle_percent") map {
      case date ~ weight ~ Some(fat) ~ Some(water) ~ Some(muscle) =>
        WeightPoint.applyRaw(date, weight, fat, water, muscle)
      case date ~ weight ~ _ ~ _ ~ _ =>
        WeightPoint.applyRaw(date, weight, 0.0, 0.0, 0.0)
    }

  private def dbWriter(weight: WeightPoint): Seq[NamedParameter] = List(
    "measure_date" -> weight.date.toString(WeightPoint.dateFormat),
    "weight_kg" -> weight.weight,
    "fat_percent" -> weight.fat,
    "water_percent" -> weight.water,
    "muscle_percent" -> weight.muscle
  )

  private def dateFilter(start: Option[String], end: Option[String]) = (start, end) match {
    case (None, None) => ""
    case (Some(dStart), None) => s" WHERE measure_date > '$dStart'"
    case (None, Some(dEnd)) => s" WHERE measure_date < '$dEnd'"
    case (Some(dStart), Some(dEnd)) => s" WHERE measure_date BETWEEN '$dStart' AND '$dEnd'"
  }

  /**
    * Fetches all the weight points and optionally filter them by date
    */
  def fetchAll(start: Option[String], end: Option[String]): Try[List[WeightPoint]] = {

    db.withConnection { implicit conn: Connection =>
      // Building the SQL to get all the points
      val stmt = s"SELECT * FROM weights ${dateFilter(start, end)} ORDER BY measure_date;"

      // Query the DB and return the value
      Try { SQL( stmt ).as( dbReader.* ) }
    }

  }

  /**
    * Fetches all the weight points and group them by week
    */
  def fetchWeekly(start: Option[String], end: Option[String]): Try[List[WeightPoint]] = {

    db.withConnection { implicit conn: Connection =>
      val stmt = s"""
          SELECT
            MAX(date(measure_date, 'weekday 0', '-6 day')) || " 00:00:00" AS measure_date,
            ROUND(AVG(weight_kg), 3) AS weight_kg,
            ROUND(AVG(fat_percent), 3) AS fat_percent,
            ROUND(AVG(water_percent), 3) AS water_percent,
            ROUND(AVG(muscle_percent), 3) AS muscle_percent
          FROM weights
          ${dateFilter( start, end )}
          GROUP BY strftime('y%Y-w%W', measure_date)
          ORDER BY measure_date;
        """

      // Query the DB and return the value
      Try { SQL( stmt ).as( dbReader.* ) }
    }

  }

  /**
    * Adds a new weight point
    */
  def insert(weight: WeightPoint): Try[Unit] = {
    val params = dbWriter(weight)
    val names = params map { _.name } mkString ", "
    val tokens = params map { p => s"{${p.name}}" } mkString ", "
    val stmt = s"INSERT INTO weights($names) VALUES ($tokens)"

    // Query the DB
    Try {
      db.withConnection { implicit conn: Connection =>
        SQL( stmt ).on( params: _* ).executeInsert()
      }
    }
  }

  /**
    * Modifies an existing weight
    */
  def update(weight: WeightPoint): Try[Int] = {
    val params = dbWriter(weight)
    val sets = params map { p => s"${p.name} = {${p.name}}" } mkString ", "
    val stmt = s"UPDATE weights SET $sets WHERE measure_date = {measure_date};"

    // Query the DB
    Try {
      db.withConnection { implicit conn: Connection =>
        SQL( stmt ).on( params: _* ).executeUpdate()
      }
    }
  }

  /**
    * Deletes an existing weight
    */
  def delete(date: String): Try[Int] = {

    db.withConnection { implicit conn: Connection =>
      // Building the SQL to delete the point
      val stmt = s"DELETE FROM weights WHERE measure_date = {measure_date};"

      // Query the DB and return the value
      Try {
        db.withConnection { implicit conn: Connection =>
          SQL( stmt ).on( "measure_date" -> date ).executeUpdate()
        }
      }
    }

  }
}
