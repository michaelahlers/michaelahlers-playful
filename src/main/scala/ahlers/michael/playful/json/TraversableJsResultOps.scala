package ahlers.michael.playful.json

import play.api.libs.json.{JsResult, JsSuccess}

import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds

class TraversableJsResultOps[E, C[X] <: Traversable[X]](results: C[JsResult[E]]) {

  def marshaled(implicit cbf: CanBuildFrom[C[JsResult[E]], E, C[E]]): JsResult[C[E]] = {
    val builder = cbf(results)
    builder.sizeHint(results)
    JsSuccess(builder.result())
  }

}

