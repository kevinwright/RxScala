package rxscala
package observables
package richops

import rxscala.ImplicitFunctionConversions._
import rx.util.functions._

class PartitioningOps[T](val obs: Observable[T]) extends AnyVal {
  /**
   * Returns an Observable that skips the first `num` items emitted by the source
   * Observable and emits the remainder.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/skip.png">
   *
   * @param n
   *            the number of items to skip
   * @return an Observable that is identical to the source Observable except that it does not
   *         emit the first `num` items that the source emits
   */
  def drop(n: Int): Observable[T] = {
    Observable[T](obs.asJavaObservable.skip(n))
  }

  /**
   * Returns an Observable that bypasses all items from the source Observable as long as the specified
   * condition holds true. Emits all further source items as soon as the condition becomes false.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/skipWhile.png">
   *
   * @param predicate
   *            A function to test each item emitted from the source Observable for a condition.
   * @return an Observable that emits all items from the source Observable as soon as the condition
   *         becomes false.
   */
  def dropWhile(predicate: T => Boolean): Observable[T] = {
    Observable(obs.asJavaObservable.skipWhile(predicate))
  }

  /**
   * Returns an Observable that emits only the first `num` items emitted by the source
   * Observable.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/take.png">
   *
   * This method returns an Observable that will invoke a subscribing [[rx.lang.scala.Observer]]'s 
   * [[rx.lang.scala.Observer.onNext onNext]] function a maximum of `num` times before invoking
   * [[rx.lang.scala.Observer.onCompleted onCompleted]].
   *
   * @param n
   *            the number of items to take
   * @return an Observable that emits only the first `num` items from the source
   *         Observable, or all of the items from the source Observable if that Observable emits
   *         fewer than `num` items
   */
  def take(n: Int): Observable[T] = {
    Observable[T](obs.asJavaObservable.take(n))
  }

  /**
   * Returns an Observable that emits items emitted by the source Observable so long as a
   * specified condition is true.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/takeWhile.png">
   *
   * @param predicate
   *            a function that evaluates an item emitted by the source Observable and returns a
   *            Boolean
   * @return an Observable that emits the items from the source Observable so long as each item
   *         satisfies the condition defined by `predicate`
   */
  def takeWhile(predicate: T => Boolean): Observable[T] = {
    Observable[T](obs.asJavaObservable.takeWhile(predicate))
  }

  /**
   * Returns an Observable that emits only the last `count` items emitted by the source
   * Observable.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/last.png">
   *
   * @param count
   *            the number of items to emit from the end of the sequence emitted by the source
   *            Observable
   * @return an Observable that emits only the last `count` items emitted by the source
   *         Observable
   */
  def takeRight(count: Int): Observable[T] = {
    Observable[T](obs.asJavaObservable.takeLast(count))
  }
  
    /**
   * Returns an Observable that emits only the very first item emitted by the source Observable, or
   * a default value if the source Observable is empty.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/firstOrDefault.png">
   *
   * @param default
   *            The default value to emit if the source Observable doesn't emit anything.
   *            This is a by-name parameter, so it is only evaluated if the source Observable doesn't emit anything.
   * @return an Observable that emits only the very first item from the source, or a default value
   *         if the source Observable completes without emitting any item.
   */
  def firstOrElse[U >: T](default: => U): Observable[U] = {
    this.take(1).foldLeft[Option[U]](None)((v: Option[U], e: U) => Some(e)).map({
      case Some(element) => element
      case None => default
    })
  }

  /**
   * Returns an Observable that emits only the very first item emitted by the source Observable, or
   * a default value if the source Observable is empty.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/firstOrDefault.png">
   *
   * @param default
   *            The default value to emit if the source Observable doesn't emit anything.
   *            This is a by-name parameter, so it is only evaluated if the source Observable doesn't emit anything.
   * @return an Observable that emits only the very first item from the source, or a default value
   *         if the source Observable completes without emitting any item.
   */
  def headOrElse[U >: T](default: => U): Observable[U] = firstOrElse(default)

  /**
   * Returns an Observable that emits only the very first item emitted by the source Observable.
   * This is just a shorthand for `take(1)`.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/first.png">
   *
   * @return an Observable that emits only the very first item from the source, or none if the
   *         source Observable completes without emitting a single item.
   */
  def first: Observable[T] = take(1)

}