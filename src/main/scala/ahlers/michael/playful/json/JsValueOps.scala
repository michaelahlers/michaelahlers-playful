package ahlers.michael.playful.json

import play.api.libs.json._

class JsValueOps(value: JsValue) {

  def materialized: List[(JsPath, JsValue)] = ???

}
