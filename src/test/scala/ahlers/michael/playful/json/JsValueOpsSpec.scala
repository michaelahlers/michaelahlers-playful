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
    "materialize" when {
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

          JsValues.materialized(exemplar) should contain theSameElementsAs expected
        }
      }
    }
  }

  "Objects" must {
    "materialize" in {
      val expected =
        __ \ 'foo \ 'null -> JsNull ::
          __ \ 'foo \ 'boolean -> toJson(true) ::
          __ \ 'bear \ 'number -> toJson(10) ::
          __ \ 'bear \ 'string -> toJson("") ::
          (__ \ 'foobear \ 'array apply 0, toJson(10)) ::
          (__ \ 'foobear \ 'array apply 1, toJson(11)) ::
          (__ \ 'foobear \ 'array apply 2, toJson(12)) ::
          Nil

      val actual =
        JsValues.materialized(obj(
          "foo" -> obj(
            "null" -> JsNull,
            "boolean" -> true
          ),
          "bear" -> obj(
            "number" -> 10,
            "string" -> ""
          ),
          "foobear" -> obj(
            "array" -> List(10, 11, 12)
          )
        ))

      actual should contain theSameElementsAs expected
    }
  }

  "Arrays of objects" must {
    "materialize" in {
      val expected =
        (JsPath(0) \ 'foo, toJson("bear")) ::
          (JsPath(0) \ 'fiz, toJson("ban")) ::
          (JsPath(1) \ 'fu, toJson("bar")) ::
          (JsPath(1) \ 'zif, toJson("nab")) ::
          Nil

      val actual =
        JsValues.materialized(arr(
          obj("foo" -> "bear", "fiz" -> "ban"),
          obj("fu" -> "bar", "zif" -> "nab")
        ))

      actual should contain theSameElementsAs expected
    }
  }

}
