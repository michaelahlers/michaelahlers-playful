package ahlers.michael.playful

import play.api.libs.json.{JsPath, JsResult, JsValue}

import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds

package object json {

  object JsValues {

    def materialized(value: JsValue): List[(JsPath, JsValue)] = syntax.withJsValueOps(value).materialized

  }

  object TraversableJsResults {

    def marshaled[E, C[X] <: Traversable[X]](results: C[JsResult[E]])(implicit cbf: CanBuildFrom[C[JsResult[E]], E, C[E]]): JsResult[C[E]] =
      syntax.withTraversableJsResultOps(results).marshaled

  }

}
