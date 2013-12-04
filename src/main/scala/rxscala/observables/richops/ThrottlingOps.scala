package rxscala
package observables
package richops

import rxscala.util._
import rxscala.ImplicitFunctionConversions._
import rx.util.functions._

import scala.concurrent.duration.Duration
import scala.concurrent.duration.TimeUnit

class ThrottlingOps[T](val obs: Observable[T]) extends AnyVal {
  /**
   * Debounces by dropping all values that are followed by newer values before the timeout value expires. The timer resets on each `onNext` call.
   *
   * NOTE: If events keep firing faster than the timeout then no data will be emitted.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/throttleWithTimeout.png">
   *
   * @param timeout
   *            The time each value has to be 'the most recent' of the [[rx.lang.scala.Observable]] to ensure that it's not dropped.
   * @param scheduler
   *            The [[rx.lang.scala.Scheduler]] to use internally to manage the timers which handle timeout for each event.
   * @return Observable which performs the throttle operation.
   * @see `Observable.debounce`
   */
  def throttleWithTimeout(timeout: Duration, scheduler: Scheduler): Observable[T] = {
    Observable[T](obs.asJavaObservable.throttleWithTimeout(timeout.length, timeout.unit, scheduler))
  }

  /**
   * Throttles by skipping value until `skipDuration` passes and then emits the next received value.
   *
   * This differs from `Observable.throttleLast` in that this only tracks passage of time whereas `Observable.throttleLast` ticks at scheduled intervals.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/throttleFirst.png">
   *
   * @param skipDuration
   *            Time to wait before sending another value after emitting last value.
   * @param scheduler
   *            The [[rx.lang.scala.Scheduler]] to use internally to manage the timers which handle timeout for each event.
   * @return Observable which performs the throttle operation.
   */
  def throttleFirst(skipDuration: Duration, scheduler: Scheduler): Observable[T] = {
    Observable[T](obs.asJavaObservable.throttleFirst(skipDuration.length, skipDuration.unit, scheduler))
  }

  /**
   * Throttles by skipping value until `skipDuration` passes and then emits the next received value.
   *
   * This differs from `Observable.throttleLast` in that this only tracks passage of time whereas `Observable.throttleLast` ticks at scheduled intervals.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/throttleFirst.png">
   *
   * @param skipDuration
   *            Time to wait before sending another value after emitting last value.
   * @return Observable which performs the throttle operation.
   */
  def throttleFirst(skipDuration: Duration): Observable[T] = {
    Observable[T](obs.asJavaObservable.throttleFirst(skipDuration.length, skipDuration.unit))
  }

  /**
   * Throttles by returning the last value of each interval defined by 'intervalDuration'.
   *
   * This differs from `Observable.throttleFirst` in that this ticks along at a scheduled interval whereas `Observable.throttleFirst` does not tick, it just tracks passage of time.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/throttleLast.png">
   *
   * @param intervalDuration
   *            Duration of windows within with the last value will be chosen.
   * @return Observable which performs the throttle operation.
   */
  def throttleLast(intervalDuration: Duration): Observable[T] = {
    Observable[T](obs.asJavaObservable.throttleLast(intervalDuration.length, intervalDuration.unit))
  }

  /**
   * Throttles by returning the last value of each interval defined by 'intervalDuration'.
   *
   * This differs from `Observable.throttleFirst` in that this ticks along at a scheduled interval whereas `Observable.throttleFirst` does not tick, it just tracks passage of time.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/throttleLast.png">
   *
   * @param intervalDuration
   *            Duration of windows within with the last value will be chosen.
   * @return Observable which performs the throttle operation.
   */
  def throttleLast(intervalDuration: Duration, scheduler: Scheduler): Observable[T] = {
    Observable[T](obs.asJavaObservable.throttleLast(intervalDuration.length, intervalDuration.unit, scheduler))
  }

    /**
   * Debounces by dropping all values that are followed by newer values before the timeout value expires. The timer resets on each `onNext` call.
   *
   * NOTE: If events keep firing faster than the timeout then no data will be emitted.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/throttleWithTimeout.png">
   *
   * $debounceVsThrottle
   *
   * @param timeout
   *            The time each value has to be 'the most recent' of the [[rx.lang.scala.Observable]] to ensure that it's not dropped.
   *
   * @return An [[rx.lang.scala.Observable]] which filters out values which are too quickly followed up with newer values.
   * @see `Observable.debounce`
   */
  def throttleWithTimeout(timeout: Duration): Observable[T] = {
    Observable[T](obs.asJavaObservable.throttleWithTimeout(timeout.length, timeout.unit))
  }

  /**
   * Debounces by dropping all values that are followed by newer values before the timeout value expires. The timer resets on each `onNext` call.
   *
   * NOTE: If events keep firing faster than the timeout then no data will be emitted.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/debounce.png">
   *
   * $debounceVsThrottle
   *
   * @param timeout
   *            The time each value has to be 'the most recent' of the [[rx.lang.scala.Observable]] to ensure that it's not dropped.
   *
   * @return An [[rx.lang.scala.Observable]] which filters out values which are too quickly followed up with newer values.
   * @see `Observable.throttleWithTimeout`
   */
  def debounce(timeout: Duration): Observable[T] = {
    Observable[T](obs.asJavaObservable.debounce(timeout.length, timeout.unit))
  }

  /**
   * Debounces by dropping all values that are followed by newer values before the timeout value expires. The timer resets on each `onNext` call.
   *
   * NOTE: If events keep firing faster than the timeout then no data will be emitted.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/debounce.png">
   *
   * $debounceVsThrottle
   *
   * @param timeout
   *            The time each value has to be 'the most recent' of the [[rx.lang.scala.Observable]] to ensure that it's not dropped.
   * @param scheduler
   *            The [[rx.lang.scala.Scheduler]] to use internally to manage the timers which handle timeout for each event.
   * @return Observable which performs the throttle operation.
   * @see `Observable.throttleWithTimeout`
   */
  def debounce(timeout: Duration, scheduler: Scheduler): Observable[T] = {
    Observable[T](obs.asJavaObservable.debounce(timeout.length, timeout.unit, scheduler))
  }
  
    /**
   * Returns an Observable that emits the results of sampling the items emitted by the source
   * Observable at a specified time interval.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/sample.png">
   *
   * @param duration the sampling rate
   * @return an Observable that emits the results of sampling the items emitted by the source
   *         Observable at the specified time interval
   */
  def sample(duration: Duration): Observable[T] = {
    Observable[T](obs.asJavaObservable.sample(duration.length, duration.unit))
  }

  /**
   * Returns an Observable that emits the results of sampling the items emitted by the source
   * Observable at a specified time interval.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/sample.png">
   *
   * @param duration the sampling rate
   * @param scheduler
   *            the [[rx.lang.scala.Scheduler]] to use when sampling
   * @return an Observable that emits the results of sampling the items emitted by the source
   *         Observable at the specified time interval
   */
  def sample(duration: Duration, scheduler: Scheduler): Observable[T] = {
    Observable[T](obs.asJavaObservable.sample(duration.length, duration.unit, scheduler))
  }

    /**
   * Returns an Observable that forwards all sequentially distinct items emitted from the source Observable.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/distinctUntilChanged.png">
   *
   * @return an Observable of sequentially distinct items
   */
  def distinctUntilChanged: Observable[T] = {
    Observable[T](obs.asJavaObservable.distinctUntilChanged)
  }

  /**
   * Returns an Observable that forwards all items emitted from the source Observable that are sequentially
   * distinct according to a key selector function.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/distinctUntilChanged.key.png">
   *
   * @param keySelector
   *            a function that projects an emitted item to a key value which is used for deciding whether an item is sequentially
   *            distinct from another one or not
   * @return an Observable of sequentially distinct items
   */
  def distinctUntilChanged[U](keySelector: T => U): Observable[T] = {
    Observable[T](obs.asJavaObservable.distinctUntilChanged[U](keySelector))
  }

  /**
   * Returns an Observable that forwards all distinct items emitted from the source Observable.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/distinct.png">
   *
   * @return an Observable of distinct items
   */
  def distinct: Observable[T] = {
    Observable[T](obs.asJavaObservable.distinct())
  }

  /**
   * Returns an Observable that forwards all items emitted from the source Observable that are distinct according
   * to a key selector function.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/distinct.key.png">
   *
   * @param keySelector
   *            a function that projects an emitted item to a key value which is used for deciding whether an item is
   *            distinct from another one or not
   * @return an Observable of distinct items
   */
  def distinct[U](keySelector: T => U): Observable[T] = {
    Observable[T](obs.asJavaObservable.distinct[U](keySelector))
  }

}