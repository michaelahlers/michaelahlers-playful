package ahlers.michael.playful.json

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.Json.{arr, obj, toJson}
import play.api.libs.json._

class JsValueOpsSpec
  extends WordSpec
          with Matchers
          with LazyLogging {

  "Materialized" when {

    "passed non-objects" must {
      val exemplars =
        JsNull ::
          toJson(true) ::
          toJson(10) ::
          toJson("string") ::
          Nil

      exemplars foreach { exemplar =>
        s"put $exemplar at empty paths" in {
          val expected = List(__ -> exemplar)
          JsValues.materialized(exemplar) should contain theSameElementsAs expected
        }
      }

    }

    "passed arrays of non-objects" must {

      "put values at indexed paths" in {
        val exemplar = List(1, 2, 3)
        val expected = exemplar.zipWithIndex.map({ case (v, i) => JsPath.apply(i) -> toJson(v) })
        JsValues.materialized(toJson(exemplar)) should contain theSameElementsAs expected
      }

    }

    "passed objects" must {

      "put leaf values on key paths" in {
        val expected =
          __ \ 'foo \ 'null -> JsNull ::
            __ \ 'foo \ 'boolean -> toJson(true) ::
            __ \ 'bear \ 'number -> toJson(10) ::
            __ \ 'bear \ 'string -> toJson("") ::
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
            )
          ))

        actual should contain theSameElementsAs expected
      }

      "put empty leaf values on key paths" in {
        val expected =
          __ \ 'empty \ 'object -> obj() ::
            __ \ 'empty \ 'array -> arr() ::
            Nil

        val actual =
          JsValues.materialized(obj(
            "empty" -> obj(
              "object" -> obj(),
              "array" -> arr()
            )
          ))

        actual should contain theSameElementsAs expected
      }

      "put array values on indexed paths" in {
        val expected =
          (__ \ 'foobear \ 'array apply 0, toJson(10)) ::
            (__ \ 'foobear \ 'array apply 1, toJson(11)) ::
            (__ \ 'foobear \ 'array apply 2, toJson(12)) ::
            Nil

        val actual =
          JsValues.materialized(obj(
            "foobear" -> obj(
              "array" -> List(10, 11, 12)
            )
          ))

        actual should contain theSameElementsAs expected
      }

    }

    "passed arrays of objects" must {

      "put leaf values on keyed paths at indexes" in {
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

}
