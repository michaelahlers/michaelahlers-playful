package ahlers.michael.playful.iteratee

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.iteratee.{Enumerator, Iteratee}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class EnumerateeFactoryOpsSpec
  extends WordSpec
          with Matchers
          with LazyLogging {

  "Zip with index" must {

    "match the iterable contract" in {
      val exemplar = List("A", "B", "C")

      val expected = exemplar.zipWithIndex

      val actual =
        Enumerator.enumerate(exemplar) &>
          EnumerateeFactoryOps.zipWithIndex |>>>
          Iteratee.getChunks

      Await.result(actual, Duration.Inf) should be(expected)
    }

    "accept any numeric and step" in {
      val expected = List("A" -> 1.0F, "B" -> 1.5F, "C" -> 2.0F)

      val actual =
        Enumerator("A", "B", "C") &>
          EnumerateeFactoryOps.zipWithIndex(1.0F, step = (i: Float) => i + 0.5F) |>>>
          Iteratee.getChunks

      Await.result(actual, Duration.Inf) should be(expected)
    }

  }

}
