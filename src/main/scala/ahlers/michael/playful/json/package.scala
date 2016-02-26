package ahlers.michael.playful

import play.api.libs.json.JsValue

package object json {

  object JsValues {

    def materialized(value: JsValue) = syntax.withJsValueOps(value).materialized

  }

}
