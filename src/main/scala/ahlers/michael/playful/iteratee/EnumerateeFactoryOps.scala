package ahlers.michael.playful.iteratee

import play.api.libs.iteratee.{Enumeratee, Enumerator, Input}

import scala.concurrent.ExecutionContext

/**
 * Factory functions augmenting those provided by the [[play.api.libs.iteratee.Enumeratee]] companion object.
 */
class EnumerateeFactoryOps(e: Enumeratee.type) {

  /**
   * As a complement to [[play.api.libs.iteratee.Enumeratee.heading]] and [[play.api.libs.iteratee.Enumeratee.trailing]], allows for inclusion of arbitrary elements between those from the producer.
   */
  def joining[E](separators: Enumerator[E])(implicit ec: ExecutionContext): Enumeratee[E, E] =
    zipWithIndex[E] compose Enumeratee.mapInputFlatten[(E, Int)] {

      case Input.Empty =>
        Enumerator.enumInput[E](Input.Empty)

      case Input.El((element, index)) if 0 < index =>
        separators andThen Enumerator(element)

      case Input.El((element, _)) =>
        Enumerator(element)

      case Input.EOF =>
        Enumerator.enumInput[E](Input.EOF)

    }

  /**
   * Zips elements with an index of the given [[scala.math.Numeric]] type, stepped by the given function.
   */
  def zipWithIndex[E, I](first: I, step: I => I)(implicit ev: Numeric[I]): Enumeratee[E, (E, I)] =
    e.scanLeft[E](null.asInstanceOf[E] -> ev.minus(first, step(ev.zero))) {
      case ((_, index), value) =>
        value -> step(index)
    }

  /**
   * Zips elements with an incrementing index of the given [[scala.math.Numeric]] type, adding one each time.
   */
  def zipWithIndex[E, I](first: I)(implicit ev: Numeric[I]): Enumeratee[E, (E, I)] = zipWithIndex(first, ev.plus(_, ev.one))

  /**
   * Zips elements with an incrementing index by the same contract [[scala.collection.GenIterableLike#zipWithIndex zipWithIndex]].
   */
  def zipWithIndex[E]: Enumeratee[E, (E, Int)] = zipWithIndex(0)

}
