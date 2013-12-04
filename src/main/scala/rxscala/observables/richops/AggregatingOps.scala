package rxscala
package observables
package richops

import rxscala.ImplicitFunctionConversions._
import rx.util.functions._

class AggregatingOps[+T](val obs: Observable[T]) extends AnyVal {

  /**
   * Returns an Observable that applies a function of your choosing to the first item emitted by a
   * source Observable, then feeds the result of that function along with the second item emitted
   * by the source Observable into the same function, and so on until all items have been emitted
   * by the source Observable, and emits the final result from the final call to your function as
   * its sole item.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/reduce.png">
   *
   * This technique, which is called "reduce" or "aggregate" here, is sometimes called "fold,"
   * "accumulate," "compress," or "inject" in other programming contexts. Groovy, for instance,
   * has an `inject` method that does a similar operation on lists.
   *
   * @param accumulator
   *            An accumulator function to be invoked on each item emitted by the source
   *            Observable, whose result will be used in the next accumulator call
   * @return an Observable that emits a single item that is the result of accumulating the
   *         output from the source Observable
   */
  def reduce[U >: T](accumulator: (U, U) => U): Observable[U] = {
    val func: Func2[_ >: U, _ >: U, _ <: U] = accumulator
    val func2 = func.asInstanceOf[Func2[T, T, T]]
    Observable[U](obs.asJavaObservable.asInstanceOf[rx.Observable[T]].reduce(func2))
  }


  /**
   * Returns an Observable that applies a function of your choosing to the first item emitted by a
   * source Observable, then feeds the result of that function along with the second item emitted
   * by an Observable into the same function, and so on until all items have been emitted by the
   * source Observable, emitting the result of each of these iterations.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/scanSeed.png">
   *
   * This sort of function is sometimes called an accumulator.
   *
   * Note that when you pass a seed to `scan()` the resulting Observable will emit
   * that seed as its first emitted item.
   *
   * @param initialValue
   *            the initial (seed) accumulator value
   * @param accumulator
   *            an accumulator function to be invoked on each item emitted by the source
   *            Observable, whose result will be emitted to [[rx.lang.scala.Observer]]s via [[rx.lang.scala.Observer.onNext onNext]] and used in the next accumulator call.
   * @return an Observable that emits the results of each call to the accumulator function
   */
  def scan[R](initialValue: R)(accumulator: (R, T) => R): Observable[R] = {
    Observable[R](obs.asJavaObservable.scan(initialValue, (t1: R, t2: T) => accumulator(t1,t2)
    ))
  }

    /**
   * Returns an Observable that emits a Boolean that indicates whether all of the items emitted by
   * the source Observable satisfy a condition.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/all.png">
   *
   * @param predicate
   *            a function that evaluates an item and returns a Boolean
   * @return an Observable that emits `true` if all items emitted by the source
   *         Observable satisfy the predicate; otherwise, `false`
   */
  def forall(predicate: T => Boolean): Observable[Boolean] = {
    // type mismatch; found : rx.Observable[java.lang.Boolean] required: rx.Observable[_ <: scala.Boolean]
    // new Observable[Boolean](asJava.all(predicate))
    // it's more fun in Scala:
    obs.map(predicate).foldLeft(true)(_ && _)
  }
  
  /** Tests whether a predicate holds for some of the elements of this `Observable`.
    *
    *  @param   p     the predicate used to test elements.
    *  @return        an Observable emitting one single Boolean, which is `true` if the given predicate `p`
    *                 holds for some of the elements of this Observable, and `false` otherwise.
    */
  def exists(p: T => Boolean): Observable[Boolean] = {
    Observable[java.lang.Boolean](obs.asJavaObservable.exists(p)).map(_.booleanValue())
  }
  
  /**
   * Groups the items emitted by this Observable according to a specified discriminator function.
   *
   * @param f
   *            a function that extracts the key from an item
   * @tparam K
   *            the type of keys returned by the discriminator function.
   * @return an Observable that emits `(key, observable)` pairs, where `observable`
   *         contains all items for which `f` returned `key`.
   */
  def groupBy[K](f: T => K): Observable[(K, Observable[T])] = {
    val o1 = obs.asJavaObservable.groupBy[K](f) : rx.Observable[_ <: rx.observables.GroupedObservable[K, _ <: T]]
    val func = (o: rx.observables.GroupedObservable[K, _ <: T]) => (o.getKey, Observable[T](o))
    Observable[(K, Observable[T])](o1.map[(K, Observable[T])](func))
  }
  
    /**
   * Returns an Observable that counts the total number of elements in the source Observable.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/count.png">
   *
   * @return an Observable emitting the number of counted elements of the source Observable
   *         as its single item.
   */
  def length: Observable[Int] = {
    Observable[Integer](obs.asJavaObservable.count()).map(_.intValue())
  }

  /**
   * Returns an Observable that counts the total number of elements in the source Observable.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/count.png">
   *
   * @return an Observable emitting the number of counted elements of the source Observable
   *         as its single item.
   */
  def size: Observable[Int] = length

}