package ahlers.michael.playful.json

import play.api.libs.json._

import scala.annotation.tailrec

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
    def traverse(queue: Seq[(Seq[PathNode], JsValue)], results: Seq[(JsPath, JsValue)]): List[(JsPath, JsValue)] =
      queue.headOption match {

        case Some((prefix, JsObject(fields))) =>
          traverse(queue.drop(1) ++ fields.map({ case (k, v) => (prefix :+ KeyPathNode(k)) -> v }), results)

        case Some((prefix, JsArray(elements))) =>
          traverse(queue.drop(1) ++ elements.zipWithIndex.map({ case (v, i) => (prefix :+ IdxPathNode(i)) -> v }), results)

        case Some((prefix, leaf)) =>
          traverse(queue.drop(1), results :+ JsPath(prefix.toList) -> leaf)

        case None =>
          results.toList

      }

    /** Using [[scala.Vector]], with [[http://docs.scala-lang.org/overviews/collections/performance-characteristics near-constant performance for head, tail, and append operations]], is fast enough for our purposes.  */
    traverse(Vector(Vector.empty -> value), Vector.empty)

  }

}
