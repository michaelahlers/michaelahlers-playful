package ahlers.michael.playful.iteratee

import play.api.libs.iteratee.Enumeratee

/**
 * Factory functions augmenting those provided by the [[play.api.libs.iteratee.Enumeratee]] companion object.
 */
class EnumerateeFactoryOps(e: Enumeratee.type) {

  /**
   * Zips elements with an index of the given [[scala.math.Numeric]] type, stepped by the given function.
   */
  def zipWithIndex[T, I](first: I, step: I => I)(implicit ev: Numeric[I]): Enumeratee[T, (T, I)] =
    e.scanLeft[T](null.asInstanceOf[T] -> ev.minus(first, step(ev.zero))) {
      case ((_, index), value) =>
        value -> step(index)
    }

  /**
   * Zips elements with an incrementing index of the given [[scala.math.Numeric]] type, adding one each time.
   */
  def zipWithIndex[T, I](first: I)(implicit ev: Numeric[I]): Enumeratee[T, (T, I)] = zipWithIndex(first, ev.plus(_, ev.one))

  /**
   * Zips elements with an incrementing index by the same contract [[scala.collection.GenIterableLike#zipWithIndex zipWithIndex]].
   */
  def zipWithIndex[T]: Enumeratee[T, (T, Int)] = zipWithIndex(0)

}
