package ahlers.michael.playful.iteratee.syntax

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.iteratee.Enumeratee

class Spec
  extends WordSpec
          with Matchers
          with LazyLogging {

  "Importing" must {

    "add factory operations" in {
      import ahlers.michael.playful.iteratee.syntax._
      Enumeratee.zipWithIndex should be(an[Enumeratee[_, _]])
    }

  }

}
