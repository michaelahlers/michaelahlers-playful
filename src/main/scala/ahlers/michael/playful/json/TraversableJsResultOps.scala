package ahlers.michael.playful.json

import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsResult, JsSuccess}

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable
import scala.collection.mutable.Builder
import scala.language.higherKinds

class TraversableJsResultOps[E, C[X] <: Traversable[X]](results: C[JsResult[E]]) {

  def marshaled(implicit cbf: CanBuildFrom[C[JsResult[E]], E, C[E]]): JsResult[C[E]] = {

    type Error = (JsPath, Seq[ValidationError])

    val values: Builder[E, C[E]] = cbf(results)
    values.sizeHint(results)

    val result =
      results.foldLeft[Either[Builder[E, C[E]], mutable.Buffer[Error]]](Left(values)) {

        case (Left(a), JsSuccess(e, _)) =>
          Left(a += e)

        case (Left(_), e: JsError) =>
          Right(mutable.Buffer.empty ++ e.errors)

        case (Right(a), JsSuccess(_, _)) =>
          Right(a)

        case (Right(a), e: JsError) =>
          Right(a ++= e.errors)

      }

    result match {
      case Left(a) => JsSuccess(a.result())
      case Right(a) => JsError(a.toSeq) ++ JsError(Nil)
    }

  }

}

