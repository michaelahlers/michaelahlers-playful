package ahlers.michael.playful.json

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.Json.{arr, toJson}
import play.api.libs.json._

class JsValueOpsSpec
  extends WordSpec
          with Matchers
          with LazyLogging {

  "Naked value" must {
    "materialize at root" when {
      val exemplars =
        toJson("string") ::
          toJson(10) ::
          toJson(10.5F) ::
          arr(1, 2, 3) ::
          toJson(true) ::
          JsNull ::
          Nil

      exemplars foreach { exemplar =>
        s"given $exemplar" in {
          val expected =
            exemplar match {
              case JsArray(elements) => elements.zipWithIndex.map({ case (e, i) => JsPath(i) -> e })
              case _ => List(__ -> exemplar)
            }

          JsValues.materialized(exemplar) should be(expected)
        }
      }
    }
  }


}
