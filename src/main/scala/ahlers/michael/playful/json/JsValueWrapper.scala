package ahlers.michael.playful.json

import play.api.libs.json.JsValue

/**
 * Performs an identical function to [[play.api.libs.json.Json.JsValueWrapper]]. The core library variant is likewise sealed and its sole implementation private, so this is duplicated likewise since it allows for convenient conventions throughout Playful's own API.
 */
sealed trait JsValueWrapper

private[json] case class JsValueWrapperImpl(field: JsValue) extends JsValueWrapper
