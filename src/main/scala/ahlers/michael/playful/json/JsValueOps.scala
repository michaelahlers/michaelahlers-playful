package ahlers.michael.playful.json

import play.api.libs.json._

import scala.annotation.tailrec
import scala.collection.mutable

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

    val queue: mutable.Queue[(JsPath, JsValue)] = mutable.Queue(__ -> value)
    val results: mutable.Buffer[(JsPath, JsValue)] = mutable.Buffer.empty

    @tailrec
    def traverse(): List[(JsPath, JsValue)] =
      if (queue.isEmpty) {
        results.toList
      } else {
        queue.dequeue match {

          case (prefix: JsPath, JsObject(fields)) =>
            queue ++= fields.map({ case (k, v) => prefix \ k -> v })
            traverse()

          case (prefix: JsPath, JsArray(elements)) =>
            queue ++= elements.zipWithIndex.map({ case (v, i) => prefix(i) -> v })
            traverse()

          case head =>
            results += head
            traverse()

        }
      }

    traverse()

  }

}
