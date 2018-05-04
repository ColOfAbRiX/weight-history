package model

import java.sql.Connection

import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.db.Database
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.util.Try

/**
  * A weight measurement point
  */
case class WeightPoint(
  date: DateTime,
  weight: Double,
  fat: Double,
  water: Double,
  muscle: Double
) {

  /**
    * Converts the WeightPoint to JSON
    */
  val toJson: JsValue = Json.toJson(this)

}

object WeightPoint {

  class DataAccess(db: Database) {

    private def dbReader: RowParser[WeightPoint] =
      get[String]("measure_date") ~
      get[Double]("weight_kg") ~
      get[Option[Double]]("fat_percent") ~
      get[Option[Double]]("water_percent") ~
      get[Option[Double]]("muscle_percent") map {
        case date ~ weight ~ Some(fat) ~ Some(water) ~ Some(muscle) =>
          WeightPoint.applyRaw(date, weight, fat, water, muscle)
      }

    private def dbWriter(weight: WeightPoint): Seq[NamedParameter] = List(
      "measure_date" -> weight.date.toString(dateFormat),
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
        val stmt =
          s"""
          SELECT * FROM weights
          ${dateFilter(start, end)}
          ORDER BY measure_date;
          """

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
            AVG(weight_kg) AS weight_kg,
            AVG(fat_percent) AS fat_percent,
            AVG(water_percent) AS water_percent,
            AVG(muscle_percent) AS muscle_percent
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
    def insert(weight: WeightPoint): Try[_] = {
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
    def update(weight: WeightPoint): Try[_] = {
      val params = dbWriter(weight)
      val sets = params map { p => s"${p.name} = {${p.name}}" } mkString ", "
      val stmt = s"UPDATE weights SET $sets WHERE measure_date = {measure_date};"

      // Query the DB
      Try {
        db.withConnection { implicit conn: Connection =>
          SQL( stmt ).on( params: _* ).execute()
        }
      }
    }

    /**
      * Deletes an existing weight
      */
    def delete(date: String): Try[_] = {

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

  /** Date format used to represent a datetime */
  private val dateFormat = "yyyy-MM-dd HH:mm:ss"

  /** Facility to convert a string into the known date format */
  private def dateFromString(date: String): DateTime =
    DateTimeFormat.forPattern(dateFormat).parseDateTime(date)

  /**
    * Creates a new WeightPoint given a string representation of the date
    */
  def applyRaw( date: String, weight: Double, fat: Double, water: Double, muscle: Double ): WeightPoint =
    WeightPoint(
      dateFromString(date), weight, fat, water, muscle
    )

  /**
    * Converts JSON to a WeightPoint
    */
  def fromJson(weight: JsValue): WeightPoint = ???

  /**
    * Converts a list of WeightPoints to JSON
    */
  def toJson(weights: Seq[WeightPoint]): JsValue = Json.toJson(
    weights map { _.toJson }
  )

  implicit val jsonReads: Reads[WeightPoint] = (
    (JsPath \ "date").read[String] and
      (JsPath \ "weight").read[Double] and
      (JsPath \ "fat").read[Double] and
      (JsPath \ "water").read[Double] and
      (JsPath \ "muscle").read[Double]
    )(WeightPoint.applyRaw _)

  implicit val jsonWrites: Writes[WeightPoint] = ( weight: WeightPoint ) => Json.obj(
    "date" -> JsString( weight.date.toString( dateFormat ) ),
    "weight" -> JsNumber( weight.weight ),
    "fat" -> JsNumber( weight.fat ),
    "water" -> JsNumber( weight.water ),
    "muscle" -> JsNumber( weight.muscle )
  )

}
