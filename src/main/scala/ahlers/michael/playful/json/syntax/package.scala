package ahlers.michael.playful.json

import play.api.libs.json.JsValue

import scala.language.implicitConversions

package object syntax {

  /**
   * Import to augment [[play.api.libs.json.JsValue]] instances with [[JsValueOps additional functions]].
   */
  implicit def withJsValueOps(v: JsValue): JsValueOps = new JsValueOps(v)

}
