/**
 * Copyright 2013 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rxscala
package observables

import rx.util.functions.FuncN
import rx.Observable.OnSubscribeFunc
import ImplicitFunctionConversions.scalaFuncNToRxFuncN
import ImplicitFunctionConversions.scalaFunction0ToRxFunc0
import ImplicitFunctionConversions.scalaFunction3ToRxFunc3
import ImplicitFunctionConversions.scalaFunction4ToRxFunc4
import ImplicitFunctionConversions.scalaSchedulerToJavaScheduler
import ImplicitFunctionConversions.toJavaSubscription
import rx.util.functions.Func0
import rx.util.functions.Func1
import rx.util.functions.Func2
import rxscala.ImplicitFunctionConversions.scalaBooleanFunction1ToRxBooleanFunc1
import rxscala.ImplicitFunctionConversions.scalaByNameParamToFunc0
import rxscala.ImplicitFunctionConversions.scalaFunction0ProducingUnitToAction0
import rxscala.ImplicitFunctionConversions.scalaFunction1ProducingUnitToAction1
import rxscala.ImplicitFunctionConversions.scalaFunction1ToRxFunc1
import rxscala.ImplicitFunctionConversions.scalaFunction2ToRxFunc2
import rxscala.ImplicitFunctionConversions.scalaSchedulerToJavaScheduler
import rxscala.ImplicitFunctionConversions.toScalaSubscription
import rxscala.Scheduler
import rxscala.subjects.Subject
import scala.collection.JavaConverters.asJavaIterableConverter
import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.collection.Seq
import scala.collection.immutable.Range
import scala.concurrent.duration.Duration
import scala.concurrent.duration.TimeUnit

/**
 * The Observable interface that implements the Reactive Pattern.
 *
 * @param asJavaObservable the underlying Java observable
 *
 * @define subscribeObserverMain
 * Call this method to subscribe an [[rx.lang.scala.Observer]] for receiving 
 * items and notifications from the Observable.
 *
 * A typical implementation of `subscribe` does the following:
 *
 * It stores a reference to the Observer in a collection object, such as a `List[T]` object.
 *
 * It returns a reference to the [[rx.lang.scala.Subscription]] interface. This enables Observers to
 * unsubscribe, that is, to stop receiving items and notifications before the Observable stops
 * sending them, which also invokes the Observer's [[rx.lang.scala.Observer.onCompleted onCompleted]] method.
 *
 * An `Observable[T]` instance is responsible for accepting all subscriptions
 * and notifying all Observers. Unless the documentation for a particular
 * `Observable[T]` implementation indicates otherwise, Observers should make no
 * assumptions about the order in which multiple Observers will receive their notifications.
 *
 * @define subscribeObserverParamObserver 
 *         the observer
 * @define subscribeObserverParamScheduler 
 *         the [[rx.lang.scala.Scheduler]] on which Observers subscribe to the Observable
 * @define subscribeAllReturn 
 *         a [[rx.lang.scala.Subscription]] reference whose `unsubscribe` method can be called to  stop receiving items
 *         before the Observable has finished sending them
 *
 * @define subscribeCallbacksMainWithNotifications
 * Call this method to receive items and notifications from this observable.
 *
 * @define subscribeCallbacksMainNoNotifications
 * Call this method to receive items from this observable.
 *
 * @define subscribeCallbacksParamOnNext 
 *         this function will be called whenever the Observable emits an item
 * @define subscribeCallbacksParamOnError
 *         this function will be called if an error occurs
 * @define subscribeCallbacksParamOnComplete
 *         this function will be called when this Observable has finished emitting items
 * @define subscribeCallbacksParamScheduler
 *         the scheduler to use
 *
 * @define debounceVsThrottle
 * Information on debounce vs throttle:
 *  - [[http://drupalmotion.com/article/debounce-and-throttle-visual-explanation]]
 *  - [[http://unscriptable.com/2009/03/20/debouncing-javascript-methods/]]
 *  - [[http://www.illyriad.co.uk/blog/index.php/2011/09/javascript-dont-spam-your-server-debounce-and-throttle/]]
 *
 *
 */
trait Observable[+T]
{
  import scala.collection.Seq
  import scala.concurrent.duration.{Duration, TimeUnit}
  import rx.util.functions._
  import rxscala.util._
  import rxscala.observables.BlockingObservable
  import rxscala.ImplicitFunctionConversions._

  def asJavaObservable: rx.Observable[_ <: T]

  /**
   * $subscribeObserverMain
   *
   * @param observer $subscribeObserverParamObserver
   * @param scheduler $subscribeObserverParamScheduler
   * @return $subscribeAllReturn
   */
  def subscribe(observer: Observer[T], scheduler: Scheduler): Subscription = {
    asJavaObservable.subscribe(observer.asJavaObserver, scheduler)
  }

  /**
   * $subscribeObserverMain
   *
   * @param observer $subscribeObserverParamObserver
   * @return $subscribeAllReturn
   */
  def subscribe(observer: Observer[T]): Subscription = {
    asJavaObservable.subscribe(observer.asJavaObserver)
  }

  /**
   * $subscribeCallbacksMainNoNotifications
   *                                                               ``
   * @param onNext $subscribeCallbacksParamOnNext
   * @return $subscribeAllReturn
   */
  def subscribe(onNext: T => Unit): Subscription = {
   asJavaObservable.subscribe(scalaFunction1ProducingUnitToAction1(onNext))
  }

  /**
   * $subscribeCallbacksMainWithNotifications
   *
   * @param onNext $subscribeCallbacksParamOnNext
   * @param onError $subscribeCallbacksParamOnError
   * @return $subscribeAllReturn
   */
  def subscribe(onNext: T => Unit, onError: Throwable => Unit): Subscription = {
    asJavaObservable.subscribe(
      scalaFunction1ProducingUnitToAction1(onNext),
      scalaFunction1ProducingUnitToAction1(onError)
    )
  }

  /**
   * $subscribeCallbacksMainWithNotifications
   *
   * @param onNext $subscribeCallbacksParamOnNext
   * @param onError $subscribeCallbacksParamOnError
   * @param onCompleted $subscribeCallbacksParamOnComplete
   * @return $subscribeAllReturn
   */
  def subscribe(onNext: T => Unit, onError: Throwable => Unit, onCompleted: () => Unit): Subscription = {
    asJavaObservable.subscribe(
      scalaFunction1ProducingUnitToAction1(onNext),
      scalaFunction1ProducingUnitToAction1(onError), 
      scalaFunction0ProducingUnitToAction0(onCompleted)
    )
  }

  /**
   * $subscribeCallbacksMainWithNotifications
   *
   * @param onNext $subscribeCallbacksParamOnNext
   * @param onError $subscribeCallbacksParamOnError
   * @param onCompleted $subscribeCallbacksParamOnComplete
   * @param scheduler $subscribeCallbacksParamScheduler
   * @return $subscribeAllReturn
   */
  def subscribe(onNext: T => Unit, onError: Throwable => Unit, onCompleted: () => Unit, scheduler: Scheduler): Subscription = {
    asJavaObservable.subscribe(scalaFunction1ProducingUnitToAction1(onNext),
      scalaFunction1ProducingUnitToAction1(onError),
      scalaFunction0ProducingUnitToAction0(onCompleted),
      scheduler)
  }

  /**
   * $subscribeCallbacksMainWithNotifications
   *
   * @param onNext $subscribeCallbacksParamOnNext
   * @param onError $subscribeCallbacksParamOnError
   * @param scheduler $subscribeCallbacksParamScheduler
   * @return $subscribeAllReturn
   */
  def subscribe(onNext: T => Unit, onError: Throwable => Unit, scheduler: Scheduler): Subscription = {
    asJavaObservable.subscribe(
      scalaFunction1ProducingUnitToAction1(onNext),
      scalaFunction1ProducingUnitToAction1(onError),
      scheduler)
  }

  /**
   * $subscribeCallbacksMainNoNotifications
   *
   * @param onNext $subscribeCallbacksParamOnNext
   * @param scheduler $subscribeCallbacksParamScheduler
   * @return $subscribeAllReturn
   */
  def subscribe(onNext: T => Unit, scheduler: Scheduler): Subscription = {
    asJavaObservable.subscribe(scalaFunction1ProducingUnitToAction1(onNext), scheduler)
  }



  /**
   * Wraps this Observable in another Observable that ensures that the resulting
   * Observable is chronologically well-behaved.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/synchronize.png">
   *
   * A well-behaved Observable does not interleave its invocations of the [[rx.lang.scala.Observer.onNext onNext]], [[rx.lang.scala.Observer.onCompleted onCompleted]], and [[rx.lang.scala.Observer.onError onError]] methods of
   * its [[rx.lang.scala.Observer]]s; it invokes `onCompleted` or `onError` only once; and it never invokes `onNext` after invoking either `onCompleted` or `onError`.
   * `synchronize` enforces this, and the Observable it returns invokes `onNext` and `onCompleted` or `onError` synchronously.
   *
   * @return an Observable that is a chronologically well-behaved version of the source
   *         Observable, and that synchronously notifies its [[rx.lang.scala.Observer]]s
   */
  def synchronize: Observable[T] = {
    Observable[T](asJavaObservable.synchronize)
  }


  

  /**
   * Returns an Observable which only emits those items for which a given predicate holds.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/filter.png">
   *
   * @param predicate
   *            a function that evaluates the items emitted by the source Observable, returning `true` if they pass the filter
   * @return an Observable that emits only those items in the original Observable that the filter
   *         evaluates as `true`
   */
  def filter(predicate: T => Boolean): Observable[T] = {
    Observable[T](asJavaObservable.filter(predicate))
  }

  
  /**
   * Returns an Observable that applies a function of your choosing to the first item emitted by a
   * source Observable, then feeds the result of that function along with the second item emitted
   * by an Observable into the same function, and so on until all items have been emitted by the
   * source Observable, emitting the final result from the final call to your function as its sole
   * item.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/reduceSeed.png">
   *
   * This technique, which is called "reduce" or "aggregate" here, is sometimes called "fold,"
   * "accumulate," "compress," or "inject" in other programming contexts. Groovy, for instance,
   * has an `inject` method that does a similar operation on lists.
   *
   * @param initialValue
   *            the initial (seed) accumulator value
   * @param accumulator
   *            an accumulator function to be invoked on each item emitted by the source
   *            Observable, the result of which will be used in the next accumulator call
   * @return an Observable that emits a single item that is the result of accumulating the output
   *         from the items emitted by the source Observable
   */
  def foldLeft[R](initialValue: R)(accumulator: (R, T) => R): Observable[R] = {
    Observable[R](this.asJavaObservable.reduce(initialValue, new Func2[R,T,R]{
      def call(t1: R, t2: T): R = accumulator(t1,t2)
    }))
  }

  /**
   * Creates a new Observable by applying a function that you supply to each item emitted by
   * the source Observable, where that function returns an Observable, and then merging those
   * resulting Observables and emitting the results of this merger.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/flatMap.png">
   *
   * @param f
   *            a function that, when applied to an item emitted by the source Observable, returns
   *            an Observable
   * @return an Observable that emits the result of applying the transformation function to each
   *         item emitted by the source Observable and merging the results of the Observables
   *         obtained from this transformation.
   */
  def flatMap[R](f: T => Observable[R]): Observable[R] = {
    Observable[R](asJavaObservable.flatMap[R](new Func1[T, rx.Observable[_ <: R]]{
      def call(t1: T): rx.Observable[_ <: R] = { f(t1).asJavaObservable }
    }))
  }

  /**
   * Returns an Observable that applies the given function to each item emitted by an
   * Observable and emits the result.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/map.png">
   *
   * @param func
   *            a function to apply to each item emitted by the Observable
   * @return an Observable that emits the items from the source Observable, transformed by the
   *         given function
   */
  def map[R](func: T => R): Observable[R] = {
    Observable[R](asJavaObservable.map[R](new Func1[T,R] {
      def call(t1: T): R = func(t1)
    }))
  }


  /**
   * Asynchronously subscribes and unsubscribes Observers on the specified [[rx.lang.scala.Scheduler]].
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/subscribeOn.png">
   *
   * @param scheduler
   *            the [[rx.lang.scala.Scheduler]] to perform subscription and unsubscription actions on
   * @return the source Observable modified so that its subscriptions and unsubscriptions happen
   *         on the specified [[rx.lang.scala.Scheduler]]
   */
  def subscribeOn(scheduler: Scheduler): Observable[T] = {
    Observable[T](asJavaObservable.subscribeOn(scheduler))
  }

  /**
   * Asynchronously notify [[rx.lang.scala.Observer]]s on the specified [[rx.lang.scala.Scheduler]].
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/observeOn.png">
   *
   * @param scheduler
   *            the [[rx.lang.scala.Scheduler]] to notify [[rx.lang.scala.Observer]]s on
   * @return the source Observable modified so that its [[rx.lang.scala.Observer]]s are notified on the
   *         specified [[rx.lang.scala.Scheduler]]
   */
  def observeOn(scheduler: Scheduler): Observable[T] = {
    Observable[T](asJavaObservable.observeOn(scheduler))
  }



  /**
   * Returns an Observable that emits a single item, a list composed of all the items emitted by
   * the source Observable.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/toList.png">
   *
   * Normally, an Observable that returns multiple items will do so by invoking its [[rx.lang.scala.Observer]]'s 
   * [[rx.lang.scala.Observer.onNext onNext]] method for each such item. You can change
   * this behavior, instructing the Observable to compose a list of all of these items and then to
   * invoke the Observer's `onNext` function once, passing it the entire list, by
   * calling the Observable's `toList` method prior to calling its `Observable.subscribe` method.
   *
   * Be careful not to use this operator on Observables that emit infinite or very large numbers
   * of items, as you do not have the option to unsubscribe.
   *
   * @return an Observable that emits a single item: a List containing all of the items emitted by
   *         the source Observable.
   */
  def toSeq: Observable[Seq[T]] = {
    Observable.jObsOfListToScObsOfSeq(asJavaObservable.toList)
      : Observable[Seq[T]] // SI-7818
  }


  /*
  
  TODO once https://github.com/Netflix/RxJava/issues/417 is fixed, we can add head and tail methods
  
  /**
   * emits NoSuchElementException("head of empty Observable") if empty
   */
  def head: Observable[T] = {
    this.take(1).fold[Option[T]](None)((v: Option[T], e: T) => Some(e)).map({
      case Some(element) => element
      case None => throw new NoSuchElementException("head of empty Observable")
    })
  }
  
  /**
   * emits an UnsupportedOperationException("tail of empty list") if empty
   */
  def tail: Observable[T] = ???
  
  */





  /**
   * Converts an Observable into a [[rx.lang.scala.observables.BlockingObservable]] (an Observable with blocking
   * operators).
   *
   * @see <a href="https://github.com/Netflix/RxJava/wiki/Blocking-Observable-Operators">Blocking Observable Operators</a>
   */
  def toBlockingObservable: BlockingObservable[T] = {
    new BlockingObservable[T](asJavaObservable.toBlockingObservable)
  }

  /**
   * Perform work in parallel by sharding an `Observable[T]` on a 
   * [[rx.lang.scala.concurrency.Schedulers.threadPoolForComputation computation]]
   * [[rx.lang.scala.Scheduler]] and return an `Observable[R]` with the output.
   *
   * @param f
   *            a function that applies Observable operators to `Observable[T]` in parallel and returns an `Observable[R]`
   * @return an Observable with the output of the function executed on a [[rx.lang.scala.Scheduler]]
   */
  def parallel[R](f: Observable[T] => Observable[R]): Observable[R] = {
    val fJava: Func1[rx.Observable[T], rx.Observable[R]] =
      (jo: rx.Observable[T]) => f(Observable[T](jo)).asJavaObservable.asInstanceOf[rx.Observable[R]]
    Observable[R](asJavaObservable.asInstanceOf[rx.Observable[T]].parallel[R](fJava))
  }

  /**
   * Perform work in parallel by sharding an `Observable[T]` on a [[rx.lang.scala.Scheduler]] and return an `Observable[R]` with the output.
   *
   * @param f
   *            a function that applies Observable operators to `Observable[T]` in parallel and returns an `Observable[R]`
   * @param scheduler
   *            a [[rx.lang.scala.Scheduler]] to perform the work on.
   * @return an Observable with the output of the function executed on a [[rx.lang.scala.Scheduler]]
   */
  def parallel[R](f: Observable[T] => Observable[R], scheduler: Scheduler): Observable[R] = {
    val fJava: Func1[rx.Observable[T], rx.Observable[R]] =
      (jo: rx.Observable[T]) => f(Observable[T](jo)).asJavaObservable.asInstanceOf[rx.Observable[R]]
    Observable[R](asJavaObservable.asInstanceOf[rx.Observable[T]].parallel[R](fJava, scheduler))
  }



  /** Tests whether this `Observable` emits no elements.
    *
    *  @return        an Observable emitting one single Boolean, which is `true` if this `Observable`
    *                 emits no elements, and `false` otherwise.
    */
  def isEmpty: Observable[Boolean] = {
    Observable[java.lang.Boolean](asJavaObservable.isEmpty).map(_.booleanValue())
  }

  def withFilter(p: T => Boolean): WithFilter[T] = {
    new WithFilter[T](p, this.asJavaObservable)
  }

}

/**
 * Provides various ways to construct new Observables.
 */
object Observable {
  import scala.collection.JavaConverters._
  import scala.collection.immutable.Range
  import scala.concurrent.duration.Duration
  import ImplicitFunctionConversions._

  private[rxscala]
  def jObsOfListToScObsOfSeq[T](jObs: rx.Observable[_ <: java.util.List[T]]): Observable[Seq[T]] = {
    val oScala1: Observable[java.util.List[T]] = new Observable[java.util.List[T]]{ def asJavaObservable = jObs }
    oScala1.map((lJava: java.util.List[T]) => lJava.asScala)
  }

  private[rxscala]
  def jObsOfJObsToScObsOfScObs[T](jObs: rx.Observable[_ <: rx.Observable[_ <: T]]): Observable[Observable[T]] = {
    val oScala1: Observable[rx.Observable[_ <: T]] = new Observable[rx.Observable[_ <: T]]{ def asJavaObservable = jObs }
    oScala1.map((oJava: rx.Observable[_ <: T]) => new Observable[T]{ def asJavaObservable = oJava})
  }

  /**
   * Creates a new Scala Observable from a given Java Observable.
   */
  def apply[T](observable: rx.Observable[_ <: T]): Observable[T] = {
    new Observable[T]{
      def asJavaObservable = observable
    }
  }

  /**
   * Creates an Observable that will execute the given function when an [[rx.lang.scala.Observer]] subscribes to it.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/create.png">
   *
   * Write the function you pass to `create` so that it behaves as an Observable: It
   * should invoke the Observer's [[rx.lang.scala.Observer.onNext onNext]], [[rx.lang.scala.Observer.onError onError]], and [[rx.lang.scala.Observer.onCompleted onCompleted]] methods
   * appropriately.
   *
   * A well-formed Observable must invoke either the Observer's `onCompleted` method
   * exactly once or its `onError` method exactly once.
   *
   * See <a href="http://go.microsoft.com/fwlink/?LinkID=205219">Rx Design Guidelines (PDF)</a>
   * for detailed information.
   *
   *
   * @tparam T
   *            the type of the items that this Observable emits
   * @param func
   *            a function that accepts an `Observer[T]`, invokes its `onNext`, `onError`, and `onCompleted` methods
   *            as appropriate, and returns a [[rx.lang.scala.Subscription]] to allow the Observer to
   *            canceling the subscription
   * @return an Observable that, when an [[rx.lang.scala.Observer]] subscribes to it, will execute the given
   *         function
   */
  def apply[T](func: Observer[T] => Subscription): Observable[T] = {
    Observable[T](rx.Observable.create(new OnSubscribeFunc[T] {
      def onSubscribe(t1: rx.Observer[_ >: T]): rx.Subscription = {
        func(Observer(t1))
      }
    }))
  }

  /**
   * Returns an Observable that invokes an [[rx.lang.scala.Observer]]'s [[rx.lang.scala.Observer.onError onError]] method when the Observer subscribes to it
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/error.png">
   *
   * @param exception
   *            the particular error to report
   * @tparam T
   *            the type of the items (ostensibly) emitted by the Observable
   * @return an Observable that invokes the [[rx.lang.scala.Observer]]'s [[rx.lang.scala.Observer.onError onError]] method when the Observer subscribes to it
   */
  def apply[T](exception: Throwable): Observable[T] = {
    Observable[T](rx.Observable.error(exception))
  }

  /**
   * Converts a sequence of values into an Observable.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/from.png">
   *
   * Implementation note: the entire array will be immediately emitted each time an [[rx.lang.scala.Observer]] subscribes.
   * Since this occurs before the [[rx.lang.scala.Subscription]] is returned,
   * it in not possible to unsubscribe from the sequence before it completes.
   *
   * @param items
   *            the source Array
   * @tparam T
   *            the type of items in the Array, and the type of items to be emitted by the
   *            resulting Observable
   * @return an Observable that emits each item in the source Array
   */
  def apply[T](items: T*): Observable[T] = {
    Observable[T](rx.Observable.from(items.toIterable.asJava))
  }

  /**
   * Generates an Observable that emits a sequence of integers within a specified range.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/range.png">
   *
   * Implementation note: the entire range will be immediately emitted each time an [[rx.lang.scala.Observer]] subscribes.
   * Since this occurs before the [[rx.lang.scala.Subscription]] is returned,
   * it in not possible to unsubscribe from the sequence before it completes.
   *
   * @param range the range
   * @return an Observable that emits a range of sequential integers
   */
  def apply(range: Range): Observable[Int] = {
    Observable[Int](rx.Observable.from(range.toIterable.asJava))
  }

  /**
   * Returns an Observable that calls an Observable factory to create its Observable for each
   * new Observer that subscribes. That is, for each subscriber, the actual Observable is determined
   * by the factory function.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/defer.png">
   *
   * The defer operator allows you to defer or delay emitting items from an Observable until such
   * time as an Observer subscribes to the Observable. This allows an [[rx.lang.scala.Observer]] to easily
   * obtain updates or a refreshed version of the sequence.
   *
   * @param observable
   *            the Observable factory function to invoke for each [[rx.lang.scala.Observer]] that
   *            subscribes to the resulting Observable
   * @tparam T
   *            the type of the items emitted by the Observable
   * @return an Observable whose [[rx.lang.scala.Observer]]s trigger an invocation of the given Observable
   *         factory function
   */
  def defer[T](observable: => Observable[T]): Observable[T] = {
    Observable[T](rx.Observable.defer[T](() => observable.asJavaObservable))
  }

  /**
   * Returns an Observable that never sends any items or notifications to an [[rx.lang.scala.Observer]].
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/never.png">
   *
   * This Observable is useful primarily for testing purposes.
   *
   * @return an Observable that never sends any items or notifications to an [[rx.lang.scala.Observer]]
   */
  def never: Observable[Nothing] = {
    Observable[Nothing](rx.Observable.never())
  }

  /**
   * Given 3 observables, returns an observable that emits Tuples of 3 elements each.
   * The first emitted Tuple will contain the first element of each source observable,
   * the second Tuple the second element of each source observable, and so on.
   *
   * @return an Observable that emits the zipped Observables
   */
  def zip[A, B, C](obA: Observable[A], obB: Observable[B], obC: Observable[C]): Observable[(A, B, C)] = {
    Observable[(A, B, C)](rx.Observable.zip[A, B, C, (A, B, C)](obA.asJavaObservable, obB.asJavaObservable, obC.asJavaObservable, (a: A, b: B, c: C) => (a, b, c)))
  }

  /**
   * Given 4 observables, returns an observable that emits Tuples of 4 elements each.
   * The first emitted Tuple will contain the first element of each source observable,
   * the second Tuple the second element of each source observable, and so on.
   *
   * @return an Observable that emits the zipped Observables
   */
  def zip[A, B, C, D](obA: Observable[A], obB: Observable[B], obC: Observable[C], obD: Observable[D]): Observable[(A, B, C, D)] = {
    Observable[(A, B, C, D)](rx.Observable.zip[A, B, C, D, (A, B, C, D)](obA.asJavaObservable, obB.asJavaObservable, obC.asJavaObservable, obD.asJavaObservable, (a: A, b: B, c: C, d: D) => (a, b, c, d)))
  }

  /**
   * Given an Observable emitting `N` source observables, returns an observable that
   * emits Seqs of `N` elements each.
   * The first emitted Seq will contain the first element of each source observable,
   * the second Seq the second element of each source observable, and so on.
   *
   * Note that the returned Observable will only start emitting items once the given
   * `Observable[Observable[T]]` has completed, because otherwise it cannot know `N`.
   *
   * @param observables
   *            An Observable emitting N source Observables
   * @return an Observable that emits the zipped Seqs
   */
  def zip[T](observables: Observable[Observable[T]]): Observable[Seq[T]] = {
    val f: FuncN[Seq[T]] = (args: Seq[java.lang.Object]) => {
      val asSeq: Seq[Object] = args.toSeq
      asSeq.asInstanceOf[Seq[T]]
    }
    val list = observables.map(_.asJavaObservable).asJavaObservable
    val o = rx.Observable.zip(list, f)
    Observable[Seq[T]](o)
  }

  /**
   * Emits `0`, `1`, `2`, `...` with a delay of `duration` between consecutive numbers.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/interval.png">
   *
   * @param duration
   *            duration between two consecutive numbers
   * @return An Observable that emits a number each time interval.
   */
  def interval(duration: Duration): Observable[Long] = {
    Observable[java.lang.Long](rx.Observable.interval(duration.length, duration.unit)).map(_.longValue())
    /*XXX*/
  }

  /**
   * Emits `0`, `1`, `2`, `...` with a delay of `duration` between consecutive numbers.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/interval.png">
   *
   * @param duration
   *            duration between two consecutive numbers
   * @param scheduler
   *            the scheduler to use
   * @return An Observable that emits a number each time interval.
   */
  def interval(duration: Duration, scheduler: Scheduler): Observable[Long] = {
    Observable[java.lang.Long](rx.Observable.interval(duration.length, duration.unit, scheduler)).map(_.longValue())
    /*XXX*/
  }

}






