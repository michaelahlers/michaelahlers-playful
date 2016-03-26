package ahlers.michael.playful.json

import monocle._
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

  def updated[T](path: JsPath, assignment: T)(implicit w: Writes[T]): JsValue = {

    def JsObjectLens(field: String, default: => JsValue) = Lens[JsValue, JsValue](_ \ field getOrElse default) {
      assignment => {

        case JsObject(fields) =>
          JsObject(fields.updated(field, assignment).toList)

        case _ =>
          obj(field -> assignment)

      }
    }

    def JsArrayLens(index: Int, default: => JsValue) = Lens[JsValue, JsValue](_ apply index getOrElse default) {
      assignment => {

        case JsArray(elements) =>
          JsArray(
            elements
              .zipWithIndex
              .map({ case (v, i) => i -> v })
              .toMap
              .updated(index, assignment)
              .toList
              .sortBy({ case (i, _) => i })
              .map({ case (_, v) => v })
          )

        case _ =>
          JsArray(assignment :: Nil)

      }
    }

    @tailrec
    def lens(queue: List[PathNode], result: Lens[JsValue, JsValue]): Lens[JsValue, JsValue] =
      queue match {

        case Nil =>
          result

        case KeyPathNode(field) :: Nil =>
          result composeLens JsObjectLens(field, obj())

        case IdxPathNode(index) :: Nil =>
          result composeLens JsArrayLens(index, arr())

        case KeyPathNode(field) :: (next: KeyPathNode) :: tail =>
          lens(next :: tail, result composeLens JsObjectLens(field, obj()))

        case KeyPathNode(field) :: (next: IdxPathNode) :: tail =>
          lens(next :: tail, result composeLens JsObjectLens(field, arr()))

        case IdxPathNode(index) :: (next: KeyPathNode) :: tail =>
          lens(next :: tail, result composeLens JsArrayLens(index, obj()))

        case IdxPathNode(index) :: (next: IdxPathNode) :: tail =>
          lens(next :: tail, result composeLens JsArrayLens(index, arr()))

      }

    lens(path.path, Lens.id) set w.writes(assignment) apply value

  }

}
