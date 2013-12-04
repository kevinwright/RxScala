package rxscala
package observables
package richops

import rxscala.util._
import rxscala.ImplicitFunctionConversions._
import rx.util.functions._

import scala.concurrent.duration.Duration
import scala.concurrent.duration.TimeUnit

class WindowingOps[T](val obs: Observable[T]) extends AnyVal {
  /**
   * Creates an Observable which produces windows of collected values. This Observable produces connected
   * non-overlapping windows. The current window is emitted and replaced with a new window when the
   * Observable produced by the specified function produces a [[rx.lang.scala.util.Closing]] object. 
   * The function will then be used to create a new Observable to listen for the end of the next
   * window.
   *
   * @param closings
   *            The function which is used to produce an [[rx.lang.scala.Observable]] for every window created.
   *            When this [[rx.lang.scala.Observable]] produces a [[rx.lang.scala.util.Closing]] object, the associated window
   *            is emitted and replaced with a new one.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces connected non-overlapping windows, which are emitted
   *         when the current [[rx.lang.scala.Observable]] created with the function argument produces a [[rx.lang.scala.util.Closing]] object.
   */
  def window(closings: () => Observable[Closing]): Observable[Observable[T]] = {
    val func : Func0[_ <: rx.Observable[_ <: Closing]] = closings().asJavaObservable
    val o1: rx.Observable[_ <: rx.Observable[_]] = obs.asJavaObservable.window(func)
    val o2 = Observable[rx.Observable[_]](o1).map((x: rx.Observable[_]) => {
      val x2 = x.asInstanceOf[rx.Observable[_ <: T]]
      Observable[T](x2)
    })
    o2
  }

  /**
   * Creates an Observable which produces windows of collected values. This Observable produces windows.
   * Chunks are created when the specified `openings` Observable produces a [[rx.lang.scala.util.Opening]] object.
   * Additionally the `closings` argument is used to create an Observable which produces [[rx.lang.scala.util.Closing]] objects. 
   * When this Observable produces such an object, the associated window is emitted.
   *
   * @param openings
   *            The [[rx.lang.scala.Observable]] which when it produces a [[rx.lang.scala.util.Opening]] object, will cause
   *            another window to be created.
   * @param closings
   *            The function which is used to produce an [[rx.lang.scala.Observable]] for every window created.
   *            When this [[rx.lang.scala.Observable]] produces a [[rx.lang.scala.util.Closing]] object, the associated window
   *            is emitted.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces windows which are created and emitted when the specified [[rx.lang.scala.Observable]]s publish certain objects.
   */
  def window(openings: Observable[Opening], closings: Opening => Observable[Closing]) = {
    Observable.jObsOfJObsToScObsOfScObs(
      obs.asJavaObservable.window(openings.asJavaObservable, (op: Opening) => closings(op).asJavaObservable))
      : Observable[Observable[T]] // SI-7818
  }

  /**
   * Creates an Observable which produces windows of collected values. This Observable produces connected
   * non-overlapping windows, each containing `count` elements. When the source Observable completes or
   * encounters an error, the current window is emitted, and the event is propagated.
   *
   * @param count
   *            The maximum size of each window before it should be emitted.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces connected non-overlapping windows containing at most
   *         `count` produced values.
   */
  def window(count: Int): Observable[Observable[T]] = {
    // this unnecessary ascription is needed because of this bug (without, compiler crashes):
    // https://issues.scala-lang.org/browse/SI-7818
    Observable.jObsOfJObsToScObsOfScObs(obs.asJavaObservable.window(count)) : Observable[Observable[T]]
  }

  /**
   * Creates an Observable which produces windows of collected values. This Observable produces windows every
   * `skip` values, each containing `count` elements. When the source Observable completes or encounters an error,
   * the current window is emitted and the event is propagated.
   *
   * @param count
   *            The maximum size of each window before it should be emitted.
   * @param skip
   *            How many produced values need to be skipped before starting a new window. Note that when `skip` and
   *            `count` are equal that this is the same operation as `window(int)`.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces windows every `skip` values containing at most
   *         `count` produced values.
   */
  def window(count: Int, skip: Int): Observable[Observable[T]] = {
    Observable.jObsOfJObsToScObsOfScObs(obs.asJavaObservable.window(count, skip))
      : Observable[Observable[T]] // SI-7818
  }

  /**
   * Creates an Observable which produces windows of collected values. This Observable produces connected
   * non-overlapping windows, each of a fixed duration specified by the `timespan` argument. When the source
   * Observable completes or encounters an error, the current window is emitted and the event is propagated.
   *
   * @param timespan
   *            The period of time each window is collecting values before it should be emitted, and
   *            replaced with a new window.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces connected non-overlapping windows with a fixed duration.
   */
  def window(timespan: Duration): Observable[Observable[T]] = {
    Observable.jObsOfJObsToScObsOfScObs(obs.asJavaObservable.window(timespan.length, timespan.unit))
      : Observable[Observable[T]] // SI-7818
  }

  /**
   * Creates an Observable which produces windows of collected values. This Observable produces connected
   * non-overlapping windows, each of a fixed duration specified by the `timespan` argument. When the source
   * Observable completes or encounters an error, the current window is emitted and the event is propagated.
   *
   * @param timespan
   *            The period of time each window is collecting values before it should be emitted, and
   *            replaced with a new window.
   * @param scheduler
   *            The [[rx.lang.scala.Scheduler]] to use when determining the end and start of a window.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces connected non-overlapping windows with a fixed duration.
   */
  def window(timespan: Duration, scheduler: Scheduler): Observable[Observable[T]] = {
    Observable.jObsOfJObsToScObsOfScObs(obs.asJavaObservable.window(timespan.length, timespan.unit, scheduler))
      : Observable[Observable[T]] // SI-7818
  }

  /**
   * Creates an Observable which produces windows of collected values. This Observable produces connected
   * non-overlapping windows, each of a fixed duration specified by the `timespan` argument or a maximum size
   * specified by the `count` argument (which ever is reached first). When the source Observable completes
   * or encounters an error, the current window is emitted and the event is propagated.
   *
   * @param timespan
   *            The period of time each window is collecting values before it should be emitted, and
   *            replaced with a new window.
   * @param count
   *            The maximum size of each window before it should be emitted.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces connected non-overlapping windows which are emitted after
   *         a fixed duration or when the window has reached maximum capacity (which ever occurs first).
   */
  def window(timespan: Duration, count: Int): Observable[Observable[T]] = {
    Observable.jObsOfJObsToScObsOfScObs(obs.asJavaObservable.window(timespan.length, timespan.unit, count))
      : Observable[Observable[T]] // SI-7818
  }

  /**
   * Creates an Observable which produces windows of collected values. This Observable produces connected
   * non-overlapping windows, each of a fixed duration specified by the `timespan` argument or a maximum size
   * specified by the `count` argument (which ever is reached first). When the source Observable completes
   * or encounters an error, the current window is emitted and the event is propagated.
   *
   * @param timespan
   *            The period of time each window is collecting values before it should be emitted, and
   *            replaced with a new window.
   * @param count
   *            The maximum size of each window before it should be emitted.
   * @param scheduler
   *            The [[rx.lang.scala.Scheduler]] to use when determining the end and start of a window.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces connected non-overlapping windows which are emitted after
   *         a fixed duration or when the window has reached maximum capacity (which ever occurs first).
   */
  def window(timespan: Duration, count: Int, scheduler: Scheduler): Observable[Observable[T]] = {
    Observable.jObsOfJObsToScObsOfScObs(obs.asJavaObservable.window(timespan.length, timespan.unit, count, scheduler))
      : Observable[Observable[T]] // SI-7818
  }

  /**
   * Creates an Observable which produces windows of collected values. This Observable starts a new window
   * periodically, which is determined by the `timeshift` argument. Each window is emitted after a fixed timespan
   * specified by the `timespan` argument. When the source Observable completes or encounters an error, the
   * current window is emitted and the event is propagated.
   *
   * @param timespan
   *            The period of time each window is collecting values before it should be emitted.
   * @param timeshift
   *            The period of time after which a new window will be created.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces new windows periodically, and these are emitted after
   *         a fixed timespan has elapsed.
   */
  def window(timespan: Duration, timeshift: Duration): Observable[Observable[T]] = {
    val span: Long = timespan.length
    val shift: Long = timespan.unit.convert(timeshift.length, timeshift.unit)
    val unit: TimeUnit = timespan.unit
    Observable.jObsOfJObsToScObsOfScObs(obs.asJavaObservable.window(span, shift, unit))
      : Observable[Observable[T]] // SI-7818
  }

  /**
   * Creates an Observable which produces windows of collected values. This Observable starts a new window
   * periodically, which is determined by the `timeshift` argument. Each window is emitted after a fixed timespan
   * specified by the `timespan` argument. When the source Observable completes or encounters an error, the
   * current window is emitted and the event is propagated.
   *
   * @param timespan
   *            The period of time each window is collecting values before it should be emitted.
   * @param timeshift
   *            The period of time after which a new window will be created.
   * @param scheduler
   *            The [[rx.lang.scala.Scheduler]] to use when determining the end and start of a window.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces new windows periodically, and these are emitted after
   *         a fixed timespan has elapsed.
   */
  def window(timespan: Duration, timeshift: Duration, scheduler: Scheduler): Observable[Observable[T]] = {
    val span: Long = timespan.length
    val shift: Long = timespan.unit.convert(timeshift.length, timeshift.unit)
    val unit: TimeUnit = timespan.unit
    Observable.jObsOfJObsToScObsOfScObs(obs.asJavaObservable.window(span, shift, unit, scheduler))
      : Observable[Observable[T]] // SI-7818
  }

}