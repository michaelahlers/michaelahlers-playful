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

  "Updated" when {

    "path is root" must {

      "return assignment value" in {
        val expected = JsString("expected")
        val actual = JsValues.updated(obj("foo" -> "bear", "fiz" -> "ban"), __, expected)
        actual should be(expected)
      }

    }

    "path is unset at no depth" must {

      "append values to objects" in {
        val expected = obj("foo" -> "bear", "fiz" -> "ban")
        val actual = JsValues.updated(obj("foo" -> "bear"), __ \ 'fiz, "ban")
        actual should be(expected)
      }

    }

    "path is unset at arbitrary depth" must {

      "append values to objects" in {
        val exemplar =
          obj(
            "foo" -> "bear",
            "one" -> obj(
              "two" -> obj(
                "five" -> "Three, sir! Three!"
              )
            )
          )

        val expected =
          obj(
            "foo" -> "bear",
            "one" -> obj(
              "two" -> obj(
                "three" -> 123,
                "five" -> "Three, sir! Three!"
              )
            )
          )

        val actual = JsValues.updated(exemplar, __ \ 'one \ 'two \ 'three, 123)
        actual should be(expected)
      }

    }

    "path is set at no depth" must {

      "append values to objects" in {
        val expected = obj("foo" -> "bear")
        val actual = JsValues.updated(obj("foo" -> "BAR"), __ \ 'foo, "bear")
        actual should be(expected)
      }

    }

    "path is set at arbitrary depth" must {

      "append values to objects" in {
        val exemplar =
          obj(
            "foo" -> "bear",
            "one" -> obj(
              "two" -> obj(
                "three" -> 456
              )
            )
          )

        val expected =
          obj(
            "foo" -> "bear",
            "one" -> obj(
              "two" -> obj(
                "three" -> 123
              )
            )
          )

        val actual = JsValues.updated(exemplar, __ \ 'one \ 'two \ 'three, 123)
        actual should be(expected)
      }

    }

    "path is unset at leaf array indexes" must {

      "append values to arrays after last element" in {
        val exemplar =
          obj(
            "foo" -> "bear",
            "names" -> arr("fizban")
          )

        val expected =
          obj(
            "foo" -> "bear",
            "names" -> arr("fizban", "zifnab")
          )

        val actual = JsValues.updated(exemplar, (__ \ 'names) apply 1, "zifnab")
        actual should be(expected)
      }

      "append values to arrays past last element" in {
        val exemplar =
          obj(
            "foo" -> "bear",
            "names" -> arr("fizban")
          )

        val expected =
          obj(
            "foo" -> "bear",
            "names" -> arr("fizban", "zifnab")
          )

        val actual = JsValues.updated(exemplar, (__ \ 'names) apply 10, "zifnab")
        actual should be(expected)
      }

    }

    "path is set at leaf array indexes" must {

      "replace values in arrays" in {
        val exemplar =
          obj(
            "foo" -> "bear",
            "names" -> arr("fizban")
          )

        val expected =
          obj(
            "foo" -> "bear",
            "names" -> arr("zifnab")
          )

        val actual = JsValues.updated(exemplar, (__ \ 'names) apply 0, "zifnab")
        actual should be(expected)
      }

      "replace values in arrays between elements" in {
        val exemplar =
          obj(
            "foo" -> "bear",
            "names" -> arr("fizban", "huma", "zifnab")
          )

        val expected =
          obj(
            "foo" -> "bear",
            "names" -> arr("fizban", "paladine", "zifnab")
          )

        val actual = JsValues.updated(exemplar, (__ \ 'names) apply 1, "paladine")
        actual should be(expected)
      }

    }

    "path is unset at internal array indexes" must {

      "append values to objects" in {
        val exemplar =
          obj(
            "foo" -> "bear",
            "names" -> arr(
              obj(
                "common" -> "fizban"
              )
            )
          )

        val expected =
          obj(
            "foo" -> "bear",
            "names" -> arr(
              obj(
                "common" -> "fizban",
                "AKA" -> "zifnab"
              )
            )
          )

        val actual = JsValues.updated(exemplar, ((__ \ 'names) apply 0) \ 'AKA, "zifnab")
        actual should be(expected)
      }

    }

    "path is set at internal array indexes" must {

      "replace values" in {
        val exemplar =
          obj(
            "foo" -> "bear",
            "names" -> arr(
              obj(
                "common" -> "fizban"
              )
            )
          )

        val expected =
          obj(
            "foo" -> "bear",
            "names" -> arr(
              obj(
                "common" -> "zifnab"
              )
            )
          )

        val actual = JsValues.updated(exemplar, ((__ \ 'names) apply 0) \ 'common, "zifnab")
        actual should be(expected)
      }

    }

    "object path is set as array" must {

      "replace values" in {
        val exemplar =
          obj(
            "foo" -> "bear",
            "names" -> arr(
              obj(
                "common" -> "fizban"
              )
            )
          )

        val expected =
          obj(
            "foo" -> "bear",
            "names" ->
              obj(
                "DG" -> "zifnab"
              )
          )

        val actual = JsValues.updated(exemplar, __ \ 'names \ 'DG, "zifnab")
        actual should be(expected)
      }

    }

    "array path is set as object" must {

      "replace values" in {
        val exemplar =
          obj(
            "foo" -> "bear",
            "names" ->
              obj(
                "DG" -> "zifnab"
              )
          )

        val expected =
          obj(
            "foo" -> "bear",
            "names" -> arr(
              obj(
                "common" -> "fizban"
              )
            )
          )

        val actual = JsValues.updated(exemplar, ((__ \ 'names) apply 0) \ 'common, "fizban")
        actual should be(expected)
      }

    }

  }

}
