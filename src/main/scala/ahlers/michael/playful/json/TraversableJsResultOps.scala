package ahlers.michael.playful.json

import play.api.libs.json.{JsError, JsResult, JsSuccess}

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable.Builder
import scala.language.higherKinds

class TraversableJsResultOps[E, C[X] <: Traversable[X]](results: C[JsResult[E]]) {

  def marshaled(implicit cbf: CanBuildFrom[C[JsResult[E]], E, C[E]]): JsResult[C[E]] = {
    val values: Builder[E, C[E]] = cbf(results)
    values.sizeHint(results)

    val result =
      results.foldLeft[JsResult[Builder[E, C[E]]]](JsSuccess(values)) {

        case (JsSuccess(a, _), JsSuccess(e, _)) =>
          JsSuccess(a += e)

        case (JsSuccess(a, _), e: JsError) =>
          e

        case (a: JsError, JsSuccess(e, _)) =>
          a

        case (a: JsError, e: JsError) =>
          JsError(a.errors ++ e.errors)

      }

    result match {
      case JsSuccess(v, p) => JsSuccess(v.result(), p)
      case e: JsError => e ++ JsError(Nil)
    }

  }

}

