package ahlers.michael.playful.json

import org.scalameter._
import play.api.libs.json._

object TraversableJsResultOpsBenchmark extends Bench.OfflineReport {

  val sizes = Gen.range("results")(25000, 100000, 25000)

  val errors: Gen[Seq[JsResult[Int]]] = for {
    size <- sizes
  } yield (0 until size).map({ i => JsError(__ \ 'path \ i.toString, s"$i error") })

  val results: Gen[Seq[JsResult[Int]]] = for {
    size <- sizes
  } yield (0 until size).map({ i => if (0 == i % 2) JsSuccess(i) else JsError(__ \ 'path \ i.toString, s"$i error") })

  val successes: Gen[Seq[JsResult[Int]]] = for {
    size <- sizes
  } yield (0 until size).map(JsSuccess(_))

  performance of "Traversable result operations" in {

    measure method "marshaling errors" in {
      using(errors) in {
        results => TraversableJsResults.marshaled(results)
      }
    }

    measure method "marshaling mixed" in {
      using(results) in {
        results => TraversableJsResults.marshaled(results)
      }
    }

    measure method "marshaling successes" in {
      using(successes) in {
        results => TraversableJsResults.marshaled(results)
      }
    }

  }
}
