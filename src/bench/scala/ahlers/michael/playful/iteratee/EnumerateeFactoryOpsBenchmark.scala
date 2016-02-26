package ahlers.michael.playful.iteratee

import org.scalameter._
import play.api.libs.iteratee.{Enumerator, Iteratee}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object EnumerateeFactoryOpsBenchmark extends Bench.OfflineReport {

  val sizes = Gen.range("elements")(25000, 100000, 25000)

  val ranges = for {
    size <- sizes
  } yield Enumerator.enumerate(0 until size)

  performance of "Enumeratee factories" in {

    measure method "joining" in {
      using(ranges) in {
        elements => Await.result(elements &> Enumeratees.joining(Enumerator(-1)) |>>> Iteratee.getChunks, Duration.Inf)
      }
    }

    measure method "zipWithIndex" in {
      using(ranges) in {
        elements => Await.result(elements &> Enumeratees.zipWithIndex |>>> Iteratee.getChunks, Duration.Inf)
      }
    }

  }
}
