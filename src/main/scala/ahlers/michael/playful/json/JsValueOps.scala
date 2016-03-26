package ahlers.michael.playful.json

import play.api.libs.json.Json.{arr, obj}
import play.api.libs.json._

import scala.annotation.tailrec
import scala.collection.immutable.Queue

class JsValueOps(value: JsValue) {

  /**
   * Produces a [[scala.List]] containing materialized paths to all leaf values (''i.e.'', not [[play.api.libs.json.JsObject]]). It will accept a leaf value (including [[play.api.libs.json.JsArray]]), positioning it on a root path. No special processing is done to preserve order of object keys, however correct array indices are guaranteed.
   *
   * To illustrate:
   * {{{
import ahlers.michael.playful.json.JsValues.materialized
import play.api.libs.json.Json._
import play.api.libs.json._

val expected =
  Set(
    (__ \ 'foo, JsString("bear")),
    (__ \ 'integers apply 0, JsNumber(10)),
    (__ \ 'integers apply 1, JsNumber(11)),
    (__ \ 'fiz \ 'ban \ 'zif, JsString("nab")),
    (__ \ 'fiz \ 'ban \ 'integers apply 0, JsNumber(100)),
    (__ \ 'fiz \ 'ban \ 'integers apply 1, JsNumber(101))
  )

val actual =
  materialized(obj(
    "foo" -> "bear",
    "fiz" -> obj(
      "ban" -> obj(
        "zif" -> "nab",
        "integers" -> arr(100, 101)
      )
    ),
    "integers" -> arr(10, 11)
  ))

assert(actual.toSet == expected)
   * }}}
   */
  def materialized: List[(JsPath, JsValue)] = {

    @tailrec
    def traverse(queue: List[(Queue[PathNode], JsValue)], results: List[(JsPath, JsValue)]): List[(JsPath, JsValue)] =
      queue match {

        case (prefix, JsObject(fields)) :: tail =>
          if (fields.isEmpty) traverse(tail, (JsPath(prefix.toList), obj()) +: results)
          else traverse(fields.map({ case (k, v) => (prefix :+ KeyPathNode(k)) -> v }) ++: tail, results)

        case (prefix, JsArray(elements)) :: tail =>
          if (elements.isEmpty) traverse(tail, (JsPath(prefix.toList), arr()) +: results)
          else traverse(elements.zipWithIndex.map({ case (v, i) => (prefix :+ IdxPathNode(i)) -> v }) ++: tail, results)

        case (prefix, leaf) :: tail =>
          traverse(tail, (JsPath(prefix.toList), leaf) +: results)

        case Nil =>
          results

      }

    traverse(List(Queue.empty -> value), Nil)

  }

  def updated[T](path: JsPath, assignment: T)(implicit w: Writes[T]): JsValue = ???

}
