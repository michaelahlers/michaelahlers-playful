package ahlers.michael.playful

import play.api.libs.iteratee.Enumeratee

package object iteratee {

  object Enumeratees extends EnumerateeFactoryOps(Enumeratee)

}
