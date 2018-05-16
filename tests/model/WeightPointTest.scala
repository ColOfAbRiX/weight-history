package model

import org.joda.time.DateTime
import org.scalatestplus.play._
import play.api.libs.json._

class WeightPointTest extends PlaySpec {

  "WeightPoint class" must {

    "create when given a string date" in {
      val result = WeightPoint.applyRaw(
        "2018-01-02 03:04:05", 6.0, 7.0, 8.0, 9.0
      )
      val expected = WeightPoint(
        new DateTime(2018, 1, 2, 3, 4, 5), 6.0, 7.0, 8.0, 9.0
      )
      result mustBe expected
    }

    "convert a weight to JSON" in {
      val result = WeightPoint(
        new DateTime(2018, 1, 2, 3, 4, 5), 6.0, 7.0, 8.0, 9.0
      ).toJson
      val expected = Json.obj(
        "date" -> "2018-01-02 03:04:05",
        "weight" -> 6.0, "fat" -> 7.0,
        "water" -> 8.0, "muscle" -> 9.0
      )
      result mustBe expected
    }

  }

  "WeightPoint object" must {

    "convert a list of weights into JSON" in {
      val result = WeightPoint.toJson(
        Seq(
          WeightPoint(new DateTime(2018, 1, 2, 3, 4, 5), 6.0, 7.0, 8.0, 9.0),
          WeightPoint(new DateTime(2017, 1, 2, 3, 4, 5), 6.0, 7.0, 8.0, 9.0)
        )
      )
      val expected = Json.arr(
        Json.obj("date" -> "2018-01-02 03:04:05", "weight" -> 6.0, "fat" -> 7.0, "water" -> 8.0, "muscle" -> 9.0),
        Json.obj("date" -> "2017-01-02 03:04:05", "weight" -> 6.0, "fat" -> 7.0, "water" -> 8.0, "muscle" -> 9.0)
      )
      result mustBe expected
    }

  }
}