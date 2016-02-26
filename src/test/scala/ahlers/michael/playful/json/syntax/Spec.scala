package ahlers.michael.playful.json.syntax

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.JsString

class Spec
  extends WordSpec
          with Matchers
          with LazyLogging {

  "Importing operations" must {
    "add materialized" in {
      JsString("string").materialized should be(a[List[_]])
    }
  }

}
