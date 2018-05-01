package model

import anorm._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json._

case class WeightPoint(
  date: DateTime,
  weight: Double,
  fat: Double,
  water: Double,
  muscle: Double
)

object WeightPoint {

  def anormParser: RowParser[WeightPoint] = RowParser[WeightPoint] {

    case Row(_, Some(date: String), Some(weight: Double), Some(fat: Double), Some(water: Double), Some(muscle: Double)) =>
      Success(WeightPoint(dateFromString(date), weight, fat, water, muscle))

    case row =>
      Error(TypeDoesNotMatch(s"Unexpected row: $row"))
  }

  implicit object WeightPointJsonFormat extends Format[WeightPoint] {

    def writes(weight: WeightPoint): JsValue = JsObject(Seq(
      "date" -> JsString(weight.date.toString("yyyy-MM-dd HH:mm:ss")),
      "weight" -> JsNumber(weight.weight),
      "fat" -> JsNumber(weight.fat),
      "water" -> JsNumber(weight.water),
      "muscle" -> JsNumber(weight.muscle)
    ))

    def reads(json: JsValue): JsResult[WeightPoint] = JsSuccess(WeightPoint(
      dateFromString((json \ "date").as[String]),
      (json \ "weight").as[Double],
      (json \ "fat").as[Double],
      (json \ "water").as[Double],
      (json \ "muscle").as[Double]
    ))

  }

  private def dateFromString(date: String): DateTime =
    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(date)
}