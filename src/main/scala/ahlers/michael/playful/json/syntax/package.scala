package ahlers.michael.playful.json

import play.api.libs.json.{JsResult, JsValue}

import scala.language.{higherKinds, implicitConversions}

package object syntax {

  /**
   * Import to augment [[play.api.libs.json.JsValue]] instances with [[JsValueOps additional functions]].
   */
  implicit def withJsValueOps(v: JsValue): JsValueOps = new JsValueOps(v)

  implicit def withTraversableJsResultOps[E, C[X] <: Traversable[X]](c: C[JsResult[E]]): TraversableJsResultOps[E, C] = new TraversableJsResultOps(c)

}
