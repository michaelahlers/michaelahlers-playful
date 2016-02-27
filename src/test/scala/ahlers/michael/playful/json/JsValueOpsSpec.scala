package ahlers.michael.playful.json

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.Json.{arr, obj, toJson}
import play.api.libs.json._

class JsValueOpsSpec
  extends WordSpec
          with Matchers
          with LazyLogging {

  "Non-object values" must {
    "materialize at root" when {
      val exemplars =
        JsNull ::
          toJson(true) ::
          toJson(10) ::
          toJson("string") ::
          arr(1, 2, 3) ::
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

  "Shallow objects" must {
    "materialize" in {
      val expected =
        __ \ 'null -> JsNull ::
          __ \ 'boolean -> toJson(true) ::
          __ \ 'number -> toJson(10) ::
          __ \ 'string -> toJson("") ::
          (__ \ 'array apply 0, toJson(10)) ::
          (__ \ 'array apply 1, toJson(11)) ::
          (__ \ 'array apply 2, toJson(12)) ::
          Nil

      val actual =
        JsValues.materialized(obj(
          "null" -> JsNull,
          "boolean" -> true,
          "number" -> 10,
          "string" -> "",
          "array" -> List(10, 11, 12)
        ))

      actual should contain theSameElementsAs expected
    }
  }

}
