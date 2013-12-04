package rxscala
package observables
package richops

import rxscala.ImplicitFunctionConversions._
import rx.util.functions._

class ErrorHandlingOps[T](val obs: Observable[T]) extends AnyVal {
  /**
   * Instruct an Observable to pass control to another Observable rather than invoking [[rx.lang.scala.Observer.onError onError]] if it encounters an error.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/onErrorResumeNext.png">
   *
   * By default, when an Observable encounters an error that prevents it from emitting the
   * expected item to its [[rx.lang.scala.Observer]], the Observable invokes its Observer's
   * `onError` method, and then quits without invoking any more of its Observer's
   * methods. The `onErrorResumeNext` method changes this behavior. If you pass a
   * function that returns an Observable (`resumeFunction`) to
   * `onErrorResumeNext`, if the original Observable encounters an error, instead of
   * invoking its Observer's `onError` method, it will instead relinquish control to
   * the Observable returned from `resumeFunction`, which will invoke the Observer's 
   * [[rx.lang.scala.Observer.onNext onNext]] method if it is able to do so. In such a case, because no
   * Observable necessarily invokes `onError`, the Observer may never know that an
   * error happened.
   *
   * You can use this to prevent errors from propagating or to supply fallback data should errors
   * be encountered.
   *
   * @param resumeFunction
   *            a function that returns an Observable that will take over if the source Observable
   *            encounters an error
   * @return the original Observable, with appropriately modified behavior
   */
  def onErrorResumeNext[U >: T](resumeFunction: Throwable => Observable[U]): Observable[U] = {
    val f: Func1[Throwable, rx.Observable[_ <: U]] = (t: Throwable) => resumeFunction(t).asJavaObservable
    val f2 = f.asInstanceOf[Func1[Throwable, rx.Observable[Nothing]]]
    Observable[U](obs.asJavaObservable.onErrorResumeNext(f2))
  }

  /**
   * Instruct an Observable to pass control to another Observable rather than invoking [[rx.lang.scala.Observer.onError onError]] if it encounters an error.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/onErrorResumeNext.png">
   *
   * By default, when an Observable encounters an error that prevents it from emitting the
   * expected item to its [[rx.lang.scala.Observer]], the Observable invokes its Observer's
   * `onError` method, and then quits without invoking any more of its Observer's
   * methods. The `onErrorResumeNext` method changes this behavior. If you pass
   * another Observable (`resumeSequence`) to an Observable's
   * `onErrorResumeNext` method, if the original Observable encounters an error,
   * instead of invoking its Observer's `onError` method, it will instead relinquish
   * control to `resumeSequence` which will invoke the Observer's [[rx.lang.scala.Observer.onNext onNext]]
   * method if it is able to do so. In such a case, because no
   * Observable necessarily invokes `onError`, the Observer may never know that an
   * error happened.
   *
   * You can use this to prevent errors from propagating or to supply fallback data should errors
   * be encountered.
   *
   * @param resumeSequence
   *            a function that returns an Observable that will take over if the source Observable
   *            encounters an error
   * @return the original Observable, with appropriately modified behavior
   */
  def onErrorResumeNext[U >: T](resumeSequence: Observable[U]): Observable[U] = {
    val rSeq1: rx.Observable[_ <: U] = resumeSequence.asJavaObservable
    val rSeq2: rx.Observable[Nothing] = rSeq1.asInstanceOf[rx.Observable[Nothing]]
    Observable[U](obs.asJavaObservable.onErrorResumeNext(rSeq2))
  }

  /**
   * Instruct an Observable to pass control to another Observable rather than invoking [[rx.lang.scala.Observer.onError onError]] if it encounters an error of type `java.lang.Exception`.
   *
   * This differs from `Observable.onErrorResumeNext` in that this one does not handle `java.lang.Throwable` or `java.lang.Error` but lets those continue through.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/onErrorResumeNext.png">
   *
   * By default, when an Observable encounters an error that prevents it from emitting the
   * expected item to its [[rx.lang.scala.Observer]], the Observable invokes its Observer's
   * `onError` method, and then quits without invoking any more of its Observer's
   * methods. The `onErrorResumeNext` method changes this behavior. If you pass
   * another Observable (`resumeSequence`) to an Observable's
   * `onErrorResumeNext` method, if the original Observable encounters an error,
   * instead of invoking its Observer's `onError` method, it will instead relinquish
   * control to `resumeSequence` which will invoke the Observer's [[rx.lang.scala.Observer.onNext onNext]]
   * method if it is able to do so. In such a case, because no
   * Observable necessarily invokes `onError`, the Observer may never know that an
   * error happened.
   *
   * You can use this to prevent errors from propagating or to supply fallback data should errors
   * be encountered.
   *
   * @param resumeSequence
   *            a function that returns an Observable that will take over if the source Observable
   *            encounters an error
   * @return the original Observable, with appropriately modified behavior
   */
  def onExceptionResumeNext[U >: T](resumeSequence: Observable[U]): Observable[U] = {
    val rSeq1: rx.Observable[_ <: U] = resumeSequence.asJavaObservable
    val rSeq2: rx.Observable[Nothing] = rSeq1.asInstanceOf[rx.Observable[Nothing]]
    Observable[U](obs.asJavaObservable.onExceptionResumeNext(rSeq2))
  }

  /**
   * Instruct an Observable to emit an item (returned by a specified function) rather than
   * invoking [[rx.lang.scala.Observer.onError onError]] if it encounters an error.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/onErrorReturn.png">
   *
   * By default, when an Observable encounters an error that prevents it from emitting the
   * expected item to its [[rx.lang.scala.Observer]], the Observable invokes its Observer's
   * `onError` method, and then quits without invoking any more of its Observer's
   * methods. The `onErrorReturn` method changes this behavior. If you pass a function
   * (`resumeFunction`) to an Observable's `onErrorReturn` method, if the
   * original Observable encounters an error, instead of invoking its Observer's
   * `onError` method, it will instead pass the return value of
   * `resumeFunction` to the Observer's [[rx.lang.scala.Observer.onNext onNext]] method.
   *
   * You can use this to prevent errors from propagating or to supply fallback data should errors
   * be encountered.
   *
   * @param resumeFunction
   *            a function that returns an item that the new Observable will emit if the source
   *            Observable encounters an error
   * @return the original Observable with appropriately modified behavior
   */
  def onErrorReturn[U >: T](resumeFunction: Throwable => U): Observable[U] = {
    val f1: Func1[Throwable, _ <: U] = resumeFunction
    val f2 = f1.asInstanceOf[Func1[Throwable, Nothing]]
    Observable[U](obs.asJavaObservable.onErrorReturn(f2))
  }
  
  /**
   * Retry subscription to origin Observable upto given retry count.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/retry.png">
   *
   * If [[rx.lang.scala.Observer.onError]] is invoked the source Observable will be re-subscribed to as many times as defined by retryCount.
   *
   * Any [[rx.lang.scala.Observer.onNext]] calls received on each attempt will be emitted and concatenated together.
   *
   * For example, if an Observable fails on first time but emits [1, 2] then succeeds the second time and
   * emits [1, 2, 3, 4, 5] then the complete output would be [1, 2, 1, 2, 3, 4, 5, onCompleted].
   *
   * @param retryCount
   *            Number of retry attempts before failing.
   * @return Observable with retry logic.
   */
  def retry(retryCount: Int): Observable[T] = {
    Observable[T](obs.asJavaObservable.retry(retryCount))
  }

  /**
   * Retry subscription to origin Observable whenever onError is called (infinite retry count).
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/retry.png">
   *
   * If [[rx.lang.scala.Observer.onError]] is invoked the source Observable will be re-subscribed to.
   *
   * Any [[rx.lang.scala.Observer.onNext]] calls received on each attempt will be emitted and concatenated together.
   *
   * For example, if an Observable fails on first time but emits [1, 2] then succeeds the second time and
   * emits [1, 2, 3, 4, 5] then the complete output would be [1, 2, 1, 2, 3, 4, 5, onCompleted].
   * @return Observable with retry logic.
   */
  def retry: Observable[T] = {
    Observable[T](obs.asJavaObservable.retry())
  }

  /**
   * Registers an function to be called when this Observable invokes [[rx.lang.scala.Observer.onCompleted onCompleted]] or [[rx.lang.scala.Observer.onError onError]].
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/finallyDo.png">
   *
   * @param action
   *            an function to be invoked when the source Observable finishes
   * @return an Observable that emits the same items as the source Observable, then invokes the function
   */
  def finallyDo(action: () => Unit): Observable[T] = {
    Observable[T](obs.asJavaObservable.finallyDo(action))
  }

}