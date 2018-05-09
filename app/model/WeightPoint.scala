package model

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.functional.syntax._
import play.api.libs.json._

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

  /** Converts the WeightPoint to JSON */
  val toJson: JsValue = Json.toJson(this)

}

object WeightPoint {

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

  /**
    * Facility to convert a string into the known date format
    */
  private def dateFromString(date: String): DateTime =
    DateTimeFormat.forPattern(dateFormat).parseDateTime(date)

  /**
    * Date format used to represent a datetime
    */
  val dateFormat = "yyyy-MM-dd HH:mm:ss"

  /**
    * Creates a new WeightPoint given a string representation of the date
    */
  def applyRaw( date: String, weight: Double, fat: Double, water: Double, muscle: Double ): WeightPoint =
    WeightPoint(
      dateFromString(date), weight, fat, water, muscle
    )

  /**
    * Converts a list of WeightPoints to JSON
    */
  def toJson(weights: Seq[WeightPoint]): JsValue = Json.toJson(
    weights map { _.toJson }
  )

}
