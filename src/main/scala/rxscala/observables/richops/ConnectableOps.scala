package rxscala
package observables
package richops

import rxscala.ImplicitFunctionConversions._
import rx.util.functions._
import rxscala.subjects.Subject

class ConnectableOps[T](val obs: Observable[T]) extends AnyVal {
  /**
   * Returns a a pair of a start function and an [[rx.lang.scala.Observable]], which waits until the start function is called before it begins emitting
   * items to those [[rx.lang.scala.Observer]]s that have subscribed to it.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/publishConnect.png">
   *
   * @return a pair of a start function and an [[rx.lang.scala.Observable]] such that when the start function
   *         is called, the Observable starts to emit items to its [[rx.lang.scala.Observer]]s
   */
  def publish: (() => Subscription, Observable[T]) = {
    val javaCO = obs.asJavaObservable.publish()
    (() => javaCO.connect(), Observable[T](javaCO))
  }
 
 /**
   * Returns a pair of a start function and an [[rx.lang.scala.Observable]] that upon calling the start function causes the source Observable to
   * push results into the specified subject.
   *
   * @param subject
   *            the `rx.lang.scala.subjects.Subject` to push source items into
   * @tparam R
   *            result type
   * @return a pair of a start function and an [[rx.lang.scala.Observable]] such that when the start function
   *         is called, the Observable starts to push results into the specified Subject
   */
  def multicast[R](subject: Subject[T, R]): (() => Subscription, Observable[R]) = {
    val javaCO = obs.asJavaObservable.multicast[R](subject.asJavaSubject)
    (() => javaCO.connect(), Observable[R](javaCO))
  }
  
  /**
   * Returns a pair of a start function and an [[rx.lang.scala.Observable]] that shares a single subscription to the underlying
   * Observable that will replay all of its items and notifications to any future [[rx.lang.scala.Observer]].
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/replay.png">
   *
   * @return a pair of a start function and an [[rx.lang.scala.Observable]] such that when the start function
   *         is called, the Observable starts to emit items to its [[rx.lang.scala.Observer]]s
   */
  def replay: (() => Subscription, Observable[T]) = {
    val javaCO = obs.asJavaObservable.replay()
    (() => javaCO.connect(), Observable[T](javaCO))
  }

    /**
   * This method has similar behavior to [[rx.lang.scala.Observable.replay]] except that this auto-subscribes to
   * the source Observable rather than returning a start function and an Observable.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/cache.png">
   *
   * This is useful when you want an Observable to cache responses and you can't control the
   * subscribe/unsubscribe behavior of all the [[rx.lang.scala.Observer]]s.
   *
   * NOTE: You sacrifice the ability to unsubscribe from the origin when you use the
   * `cache()` operator so be careful not to use this operator on Observables that
   * emit an infinite or very large number of items that will use up memory.
   *
   * @return an Observable that when first subscribed to, caches all of its notifications for
   *         the benefit of subsequent subscribers.
   */
  def cache: Observable[T] = {
    Observable[T](obs.asJavaObservable.cache())
  }


  // TODO add Scala-like aggregate function
  
}