package ahlers.michael.playful.json

import monocle._
import play.api.libs.json.Json.{arr, obj}
import play.api.libs.json._

import scala.annotation.tailrec
import scala.collection.immutable.Queue

class JsValueOps(subject: JsValue) {

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

    traverse(List(Queue.empty -> subject), Nil)

  }

  /**
   * Returns a [[play.api.libs.json.JsValue]] reflecting the given updates.
   *
   * To illustrate:
   * {{{
import ahlers.michael.playful.json.JsValues.updated
import play.api.libs.json.Json._
import play.api.libs.json._

val sample =
  obj(
    "persons" -> arr(
      obj(
        "name" -> "J. Smith",
        "addresses" -> arr(
          obj(
            "street" -> "123 Oak Street",
            "city" -> "Oakmont"
          )
        )
      )
    )
  )

val expected =
  obj(
    "persons" -> arr(
      obj(
        "name" -> "John Smith",
        "addresses" -> arr(
          obj(
            "street" -> "123 Oak Street",
            "city" -> "Oakmont",
            "state" -> "PA"
          )
        )
      ),
      obj(
        "name" -> "Jane Smith"
      )
    )
  )

val actual =
  updated(sample,
    (__ \ 'persons apply 0) \ 'name -> "John Smith",
    ((__ \ 'persons apply 0) \ 'addresses apply 0) \ 'state -> "PA",
    (__ \ 'persons apply 1) \ 'name -> "Jane Smith"
  )

assert(actual == expected)
   * }}}
   *
   * @see [[https://github.com/playframework/playframework/issues/943 Play Framework Issue 943]]
   */
  def updated[T](updates: (JsPath, JsValueWrapper)*): JsValue = {

    def KeyPathLens(field: String, alternate: JsValue) = Lens[JsValue, JsValue](_ \ field getOrElse alternate) {
      assignment => {

        case JsObject(fields) =>
          JsObject(fields.updated(field, assignment).toList)

        case _ =>
          obj(field -> assignment)

      }
    }

    def IndexPathLens(index: Int, alternate: JsValue) = Lens[JsValue, JsValue](_ apply index getOrElse alternate) {
      assignment => {

        case JsArray(elements) =>
          JsArray((elements.take(index) :+ assignment) ++ elements.drop(index + 1))

        case _ =>
          JsArray(assignment :: Nil)

      }
    }

    def toAlternate: PartialFunction[PathNode, JsValue] = {
      case KeyPathNode(_) => obj()
      case IdxPathNode(_) => arr()
    }

    @tailrec
    def follow(queue: List[PathNode], result: Lens[JsValue, JsValue]): Lens[JsValue, JsValue] =
      queue match {

        case Nil =>
          result

        case KeyPathNode(field) :: tail =>
          val alternate: JsValue = tail.headOption map toAlternate getOrElse obj()
          follow(tail, result composeLens KeyPathLens(field, alternate))

        case IdxPathNode(index) :: tail =>
          val alternate: JsValue = tail.headOption map toAlternate getOrElse arr()
          follow(tail, result composeLens IndexPathLens(index, alternate))

        case RecursiveSearch(_) :: tail =>
          throw new IllegalArgumentException(s"""No meaningful update may be applied for paths containing a recursive search.""")

      }

    updates.foldLeft(subject) { case (a, (path, JsValueWrapperImpl(value))) =>
      follow(path.path, Lens.id) set value apply a
    }

  }

}
