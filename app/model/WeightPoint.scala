package model

import anorm._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
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
)

object WeightPoint {

  /** Date format used to represent a datetime */
  private val dateFormat = "yyyy-MM-dd HH:mm:ss"

  /** Facility to convert a string into the known date format */
  private def dateFromString(date: String): DateTime =
    DateTimeFormat.forPattern(dateFormat).parseDateTime(date)

  /**
    * JSON to and from conversion
    */
  implicit object JsonFormat extends Format[WeightPoint] {

    /** Creates JSON from the object */
    def writes(weight: WeightPoint): JsValue = Json.obj(
      "date" -> JsString(weight.date.toString(dateFormat)),
      "weight" -> JsNumber(weight.weight),
      "fat" -> JsNumber(weight.fat),
      "water" -> JsNumber(weight.water),
      "muscle" -> JsNumber(weight.muscle)
    )

    /** Creates the object from JSON */
    def reads(json: JsValue): JsResult[WeightPoint] = {
      Seq(
        (json \ "date").asOpt[String],
        (json \ "weight").asOpt[Double],
        (json \ "fat").asOpt[Double],
        (json \ "water").asOpt[Double],
        (json \ "muscle").asOpt[Double]
      ) match {
        case Some(date: String) :: Some(weight: Double) :: Some(fat: Double) :: Some(water: Double) :: Some(muscle: Double) :: Nil =>
          JsSuccess(WeightPoint(
            dateFromString(date), weight, fat, water, muscle
          ))
        case _ =>
          JsError("Missing elements in JSON")
      }
    }

  }

  def dbReader: RowParser[WeightPoint] = RowParser[WeightPoint] {

    case Row(date: String, Some(weight: Double), Some(fat: Double), Some(water: Double), Some(muscle: Double)) =>
      Success(WeightPoint(dateFromString(date), weight, fat, water, muscle))

    case row =>
      Error(TypeDoesNotMatch(s"Unexpected row: $row"))

  }

  def dbWriter(weight: WeightPoint): Seq[NamedParameter] = List(
    "measure_date" -> weight.date.toString(dateFormat),
    "weight_kg" -> weight.weight,
    "fat_percent" -> weight.fat,
    "water_percent" -> weight.water,
    "muscle_percent" -> weight.muscle
  )

}