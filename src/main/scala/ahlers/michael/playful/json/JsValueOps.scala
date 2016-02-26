package ahlers.michael.playful.json

import play.api.libs.json._

import scala.annotation.tailrec

class JsValueOps(value: JsValue) {

  def materialized: List[(JsPath, JsValue)] = {

    @tailrec
    def traverse(queue: List[(JsPath, JsValue)], results: List[(JsPath, JsValue)]): List[(JsPath, JsValue)] =
      queue match {

        case (prefix: JsPath, JsObject(fields)) :: tail =>
          traverse(tail ++ fields.map({ case (k, v) => prefix \ k -> v }), results)

        case (prefix: JsPath, JsArray(elements)) :: tail =>
          traverse(tail ++ elements.zipWithIndex.map({ case (v, i) => prefix(i) -> v }), results)

        case head :: tail =>
          traverse(tail, results :+ head)

        case Nil =>
          results

      }

    traverse(List(__ -> value), Nil)

  }

}
