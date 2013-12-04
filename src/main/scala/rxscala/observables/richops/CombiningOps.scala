package rxscala
package observables
package richops

import rx.util.functions._
import rxscala.ImplicitFunctionConversions._


class CombiningOps[T](val obs: Observable[T]) extends AnyVal {
  /**
   * Returns an Observable that emits the items emitted by several Observables, one after the
   * other.
   *
   * This operation is only available if `this` is of type `Observable[Observable[U]]` for some `U`,
   * otherwise you'll get a compilation error.
   *
   * @usecase def concat[U]: Observable[U]
   *    @inheritdoc
   */
  def concat[U](implicit evidence: Observable[T] <:< Observable[Observable[U]]): Observable[U] = {
    val o2: Observable[Observable[U]] = obs
    val o3: Observable[rx.Observable[_ <: U]] = o2.map(_.asJavaObservable)
    val o4: rx.Observable[_ <: rx.Observable[_ <: U]] = o3.asJavaObservable
    val o5 = rx.Observable.concat[U](o4)
    Observable[U](o5)
  }
  
  
  /**
   * Returns an Observable that first emits the items emitted by `this`, and then the items emitted
   * by `that`.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/concat.png">
   *
   * @param that
   *            an Observable to be appended
   * @return an Observable that emits items that are the result of combining the items emitted by
   *         this and that, one after the other
   */
  def ++[U >: T](that: Observable[U]): Observable[U] = {
    val o1: rx.Observable[_ <: U] = obs.asJavaObservable
    val o2: rx.Observable[_ <: U] = that.asJavaObservable
    Observable(rx.Observable.concat(o1, o2))
  }
  
    /**
   * Flattens two Observables into one Observable, without any transformation.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/merge.png">
   *
   * You can combine items emitted by two Observables so that they act like a single
   * Observable by using the `merge` method.
   *
   * @param that
   *            an Observable to be merged
   * @return an Observable that emits items from `this` and `that` until 
   *            `this` or `that` emits `onError` or `onComplete`.
   */
  def merge[U >: T](that: Observable[U]): Observable[U] = {
    val thisJava: rx.Observable[_ <: U] = obs.asJavaObservable
    val thatJava: rx.Observable[_ <: U] = that.asJavaObservable
    Observable[U](rx.Observable.merge(thisJava, thatJava))
  }

  /**
   * This behaves like [[rx.lang.scala.Observable.merge]] except that if any of the merged Observables
   * notify of an error via [[rx.lang.scala.Observer.onError onError]], `mergeDelayError` will
   * refrain from propagating that error notification until all of the merged Observables have
   * finished emitting items.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/mergeDelayError.png">
   *
   * Even if multiple merged Observables send `onError` notifications, `mergeDelayError` will only invoke the `onError` method of its
   * Observers once.
   *
   * This method allows an Observer to receive all successfully emitted items from all of the
   * source Observables without being interrupted by an error notification from one of them.
   *
   * @param that
   *            an Observable to be merged
   * @return an Observable that emits items that are the result of flattening the items emitted by
   *         `this` and `that`
   */
  def mergeDelayError[U >: T](that: Observable[U]): Observable[U] = {
    Observable[U](rx.Observable.mergeDelayError[U](obs.asJavaObservable, that.asJavaObservable))
  }

  /**
   * Flattens the sequence of Observables emitted by `this` into one Observable, without any
   * transformation.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/merge.png">
   *
   * You can combine the items emitted by multiple Observables so that they act like a single
   * Observable by using this method.
   *
   * This operation is only available if `this` is of type `Observable[Observable[U]]` for some `U`,
   * otherwise you'll get a compilation error.
   *
   * @return an Observable that emits items that are the result of flattening the items emitted
   *         by the Observables emitted by `this`
   *
   * @usecase def flatten[U]: Observable[U]
   *   @inheritdoc
   */
  def flatten[U](implicit evidence: Observable[T] <:< Observable[Observable[U]]): Observable[U] = {
    val o2: Observable[Observable[U]] = obs
    val o3: Observable[rx.Observable[_ <: U]] = o2.map(_.asJavaObservable)
    val o4: rx.Observable[_ <: rx.Observable[_ <: U]] = o3.asJavaObservable
    val o5 = rx.Observable.merge[U](o4)
    Observable[U](o5)
  }

  /**
   * This behaves like `flatten` except that if any of the merged Observables
   * notify of an error via [[rx.lang.scala.Observer.onError onError]], this method will
   * refrain from propagating that error notification until all of the merged Observables have
   * finished emitting items.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/mergeDelayError.png">
   *
   * Even if multiple merged Observables send `onError` notifications, this method will only invoke the `onError` method of its
   * Observers once.
   *
   * This method allows an Observer to receive all successfully emitted items from all of the
   * source Observables without being interrupted by an error notification from one of them.
   *
   * This operation is only available if `this` is of type `Observable[Observable[U]]` for some `U`,
   * otherwise you'll get a compilation error.
   *
   * @return an Observable that emits items that are the result of flattening the items emitted by
   *         the Observables emitted by the this Observable
   *
   * @usecase def flattenDelayError[U]: Observable[U]
   *   @inheritdoc
   */
  def flattenDelayError[U](implicit evidence: Observable[T] <:< Observable[Observable[U]]): Observable[U] = {
    val o2: Observable[Observable[U]] = obs
    val o3: Observable[rx.Observable[_ <: U]] = o2.map(_.asJavaObservable)
    val o4: rx.Observable[_ <: rx.Observable[_ <: U]] = o3.asJavaObservable
    val o5 = rx.Observable.mergeDelayError[U](o4)
    Observable[U](o5)
  }

  /**
   * Combines two observables, emitting a pair of the latest values of each of
   * the source observables each time an event is received from one of the source observables, where the
   * aggregation is defined by the given function.
   *
   * @param that
   *            The second source observable.
   * @return An Observable that combines the source Observables
   */
  def combineLatest[U](that: Observable[U]): Observable[(T, U)] = {
    val f: Func2[_ >: T, _ >: U, _ <: (T, U)] = (t: T, u: U) => (t, u)
    Observable[(T, U)](rx.Observable.combineLatest[T, U, (T, U)](obs.asJavaObservable, that.asJavaObservable, f))
  }

    /**
   * Given an Observable that emits Observables, creates a single Observable that
   * emits the items emitted by the most recently published of those Observables.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/switchDo.png">
   *
   * This operation is only available if `this` is of type `Observable[Observable[U]]` for some `U`,
   * otherwise you'll get a compilation error.
   *
   * @return an Observable that emits only the items emitted by the most recently published
   *         Observable
   *
   * @usecase def switch[U]: Observable[U]
   *   @inheritdoc
   */
  def switch[U](implicit evidence: Observable[T] <:< Observable[Observable[U]]): Observable[U] = {
    val o2: Observable[Observable[U]] = obs
    val o3: Observable[rx.Observable[_ <: U]] = o2.map(_.asJavaObservable)
    val o4: rx.Observable[_ <: rx.Observable[_ <: U]] = o3.asJavaObservable
    val o5 = rx.Observable.switchOnNext[U](o4)
    Observable[U](o5)
  }
  // Naming: We follow C# (switch), not Java (switchOnNext), because Java just had to avoid clash with keyword

    /**
   * Returns an Observable that emits the items from the source Observable only until the
   * `other` Observable emits an item.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/takeUntil.png">
   *
   * @param that
   *            the Observable whose first emitted item will cause `takeUntil` to stop
   *            emitting items from the source Observable
   * @tparam E
   *            the type of items emitted by `other`
   * @return an Observable that emits the items of the source Observable until such time as
   *         `other` emits its first item
   */
  def takeUntil[E](that: Observable[E]): Observable[T] = {
    Observable[T](obs.asJavaObservable.takeUntil(that.asJavaObservable))
  }
  
  /**
   * Wraps each item emitted by a source Observable in a timestamped tuple.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/timestamp.png">
   *
   * @return an Observable that emits timestamped items from the source Observable
   */
  def timestamp: Observable[(Long, T)] = {
    Observable[rx.util.Timestamped[_ <: T]](obs.asJavaObservable.timestamp())
      .map((t: rx.util.Timestamped[_ <: T]) => (t.getTimestampMillis, t.getValue))
  }

  /**
   * Returns an Observable formed from this Observable and another Observable by combining 
   * corresponding elements in pairs. 
   * The number of `onNext` invocations of the resulting `Observable[(T, U)]`
   * is the minumum of the number of `onNext` invocations of `this` and `that`. 
   */
  def zip[U](that: Observable[U]): Observable[(T, U)] = {
    Observable[(T, U)](rx.Observable.zip[T, U, (T, U)](obs.asJavaObservable, that.asJavaObservable, (t: T, u: U) => (t, u)))
  }

  /**
   * Zips this Observable with its indices.
   *
   * @return An Observable emitting pairs consisting of all elements of this Observable paired with 
   *         their index. Indices start at 0.
   */
  def zipWithIndex: Observable[(T, Int)] = {
    val fScala: (T, Integer) => (T, Int) = (elem: T, index: Integer) => (elem, index)
    val fJava : Func2[_ >: T, Integer, _ <: (T, Int)] = fScala
    Observable[(T, Int)](obs.asJavaObservable.mapWithIndex[(T, Int)](fJava))
  }

}