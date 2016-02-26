package ahlers.michael.playful.iteratee.syntax

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.iteratee.{Enumeratee, Enumerator}

import scala.concurrent.ExecutionContext.Implicits.global

class Spec
  extends WordSpec
          with Matchers
          with LazyLogging {

  "Importing factory operations" must {
    "add zip with index" in {
      Enumeratee.zipWithIndex should be(an[Enumeratee[_, _]])
    }
    "add joining" in {
      Enumeratee.joining(Enumerator.empty) should be(an[Enumeratee[_, _]])
    }
  }

}
