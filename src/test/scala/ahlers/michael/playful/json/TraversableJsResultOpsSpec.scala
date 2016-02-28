package ahlers.michael.playful.json

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json._

class TraversableJsResultOpsSpec
  extends WordSpec
          with Matchers
          with LazyLogging {

  "Result collections" must {

    val successes: Seq[JsResult[Int]] = (0 until 2).map(JsSuccess(_))
    val errors: Seq[JsResult[Int]] = (0 until 2).map({ i => JsError(__ \ 'path \ i.toString, s"$i error") })

    "marshal all errors" in {
      val expected = errors.collect({ case e: JsError => e }).reduce[JsError](JsError.merge)
      val actual = TraversableJsResults.marshaled(errors)
      actual should be(expected)
    }

    "marshal mixed results" in {
      val expected = errors.collect({ case e: JsError => e }).reduce[JsError](JsError.merge)
      val actual = TraversableJsResults.marshaled(successes ++ errors)
      actual should be(expected)
    }

    "marshal all successes" in {
      val expected: JsSuccess[Seq[Int]] = JsSuccess(successes.collect({ case JsSuccess(v, _) => v }))
      val actual = TraversableJsResults.marshaled(successes)
      actual should be(expected)
    }

  }

}
