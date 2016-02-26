package ahlers.michael.playful.iteratee

import play.api.libs.iteratee.Enumeratee

import scala.language.implicitConversions

/**
 * Import to augment [[play.api.libs.iteratee.Enumeratee]]'s companion object with [[EnumerateeFactoryOps additional factory functions]].
 */
package object syntax {

  /**
   * Import to augment [[play.api.libs.iteratee.Enumeratee]]'s companion object with [[EnumerateeFactoryOps additional factory functions]].
   */
  implicit def withFactoryOps(e: Enumeratee.type): EnumerateeFactoryOps = new EnumerateeFactoryOps(e)

}
