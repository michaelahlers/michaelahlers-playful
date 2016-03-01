package ahlers.michael.playful.json

import org.scalameter._
import play.api.libs.json._

object JsValueOpsBenchmark extends Bench.OfflineReport {

  val sizes = Gen.range("results")(10000, 100000, 10000)

  val flat: Gen[JsValue] = for {
    size <- sizes
  } yield JsObject((0 until size).map({ i => s"path$i" -> JsString(s"value$i") }))

  val deep: Gen[JsValue] = for {
    size <- sizes
  } yield (size until 0 by -1).foldLeft[JsValue](JsString("value")) { case (a, i) => JsObject(List(s"path$i" -> a)) }

  performance of "Value operations" in {

    measure method "materialize flat objects" in {
      using(flat) in { value =>
        JsValues.materialized(value)
      }
    }

    measure method "materialize deep objects" in {
      using(deep) in { value =>
        JsValues.materialized(value)
      }
    }

  }
}
