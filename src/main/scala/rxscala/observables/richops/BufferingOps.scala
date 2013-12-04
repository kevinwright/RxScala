package rxscala
package observables
package richops

import rxscala.util._
import rxscala.ImplicitFunctionConversions._
import rx.util.functions._

import scala.concurrent.duration.Duration
import scala.concurrent.duration.TimeUnit

class BufferingOps[T](val obs: Observable[T]) extends AnyVal {
/**
   * Creates an Observable which produces buffers of collected values.
   *
   * This Observable produces connected non-overlapping buffers. The current buffer is
   * emitted and replaced with a new buffer when the Observable produced by the specified function produces a [[rx.lang.scala.util.Closing]] object. The function will then
   * be used to create a new Observable to listen for the end of the next buffer.
   *
   * @param closings
   *            The function which is used to produce an [[rx.lang.scala.Observable]] for every buffer created.
   *            When this [[rx.lang.scala.Observable]] produces a [[rx.lang.scala.util.Closing]] object, the associated buffer
   *            is emitted and replaced with a new one.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces connected non-overlapping buffers, which are emitted
   *         when the current [[rx.lang.scala.Observable]] created with the function argument produces a [[rx.lang.scala.util.Closing]] object.
   */
  def buffer(closings: () => Observable[Closing]) : Observable[Seq[T]] = {
    val f: Func0[_ <: rx.Observable[_ <: Closing]] = closings().asJavaObservable
    val jObs: rx.Observable[_ <: java.util.List[_]] = obs.asJavaObservable.buffer(f)
    Observable.jObsOfListToScObsOfSeq(jObs.asInstanceOf[rx.Observable[_ <: java.util.List[T]]])
  }

  /**
   * Creates an Observable which produces buffers of collected values.
   *
   * This Observable produces buffers. Buffers are created when the specified `openings`
   * Observable produces a [[rx.lang.scala.util.Opening]] object. Additionally the function argument
   * is used to create an Observable which produces [[rx.lang.scala.util.Closing]] objects. When this
   * Observable produces such an object, the associated buffer is emitted.
   *
   * @param openings
   *            The [[rx.lang.scala.Observable]] which, when it produces a [[rx.lang.scala.util.Opening]] object, will cause
   *            another buffer to be created.
   * @param closings
   *            The function which is used to produce an [[rx.lang.scala.Observable]] for every buffer created.
   *            When this [[rx.lang.scala.Observable]] produces a [[rx.lang.scala.util.Closing]] object, the associated buffer
   *            is emitted.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces buffers which are created and emitted when the specified [[rx.lang.scala.Observable]]s publish certain objects.
   */
  def buffer(openings: Observable[Opening], closings: Opening => Observable[Closing]): Observable[Seq[T]] = {
    val opening: rx.Observable[_ <: Opening] = openings.asJavaObservable
    val closing: Func1[Opening, _ <: rx.Observable[_ <: Closing]] = (o: Opening) => closings(o).asJavaObservable
    val jObs: rx.Observable[_ <: java.util.List[_]] = obs.asJavaObservable.buffer(opening, closing)
    Observable.jObsOfListToScObsOfSeq(jObs.asInstanceOf[rx.Observable[_ <: java.util.List[T]]])
  }

  /**
   * Creates an Observable which produces buffers of collected values.
   *
   * This Observable produces connected non-overlapping buffers, each containing `count`
   * elements. When the source Observable completes or encounters an error, the current
   * buffer is emitted, and the event is propagated.
   *
   * @param count
   *            The maximum size of each buffer before it should be emitted.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces connected non-overlapping buffers containing at most
   *         `count` produced values.
   */
  def buffer(count: Int): Observable[Seq[T]] = {
    val oJava: rx.Observable[_ <: java.util.List[_]] = obs.asJavaObservable.buffer(count)
    Observable.jObsOfListToScObsOfSeq(oJava.asInstanceOf[rx.Observable[_ <: java.util.List[T]]])
  }

  /**
   * Creates an Observable which produces buffers of collected values.
   *
   * This Observable produces buffers every `skip` values, each containing `count`
   * elements. When the source Observable completes or encounters an error, the current
   * buffer is emitted, and the event is propagated.
   *
   * @param count
   *            The maximum size of each buffer before it should be emitted.
   * @param skip
   *            How many produced values need to be skipped before starting a new buffer. Note that when `skip` and
   *            `count` are equals that this is the same operation as `buffer(int)`.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces buffers every `skip` values containing at most
   *         `count` produced values.
   */
  def buffer(count: Int, skip: Int): Observable[Seq[T]] = {
    val oJava: rx.Observable[_ <: java.util.List[_]] = obs.asJavaObservable.buffer(count, skip)
    Observable.jObsOfListToScObsOfSeq(oJava.asInstanceOf[rx.Observable[_ <: java.util.List[T]]])
  }

  /**
   * Creates an Observable which produces buffers of collected values.
   *
   * This Observable produces connected non-overlapping buffers, each of a fixed duration
   * specified by the `timespan` argument. When the source Observable completes or encounters
   * an error, the current buffer is emitted and the event is propagated.
   *
   * @param timespan
   *            The period of time each buffer is collecting values before it should be emitted, and
   *            replaced with a new buffer.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces connected non-overlapping buffers with a fixed duration.
   */
  def buffer(timespan: Duration): Observable[Seq[T]] = {
    val oJava: rx.Observable[_ <: java.util.List[_]] = obs.asJavaObservable.buffer(timespan.length, timespan.unit)
    Observable.jObsOfListToScObsOfSeq(oJava.asInstanceOf[rx.Observable[_ <: java.util.List[T]]])
  }

  /**
   * Creates an Observable which produces buffers of collected values.
   *
   * This Observable produces connected non-overlapping buffers, each of a fixed duration
   * specified by the `timespan` argument. When the source Observable completes or encounters
   * an error, the current buffer is emitted and the event is propagated.
   *
   * @param timespan
   *            The period of time each buffer is collecting values before it should be emitted, and
   *            replaced with a new buffer.
   * @param scheduler
   *            The [[rx.lang.scala.Scheduler]] to use when determining the end and start of a buffer.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces connected non-overlapping buffers with a fixed duration.
   */
  def buffer(timespan: Duration, scheduler: Scheduler): Observable[Seq[T]] = {
    val oJava: rx.Observable[_ <: java.util.List[_]] = obs.asJavaObservable.buffer(timespan.length, timespan.unit, scheduler)
    Observable.jObsOfListToScObsOfSeq(oJava.asInstanceOf[rx.Observable[_ <: java.util.List[T]]])
  }

  /**
   * Creates an Observable which produces buffers of collected values. This Observable produces connected
   * non-overlapping buffers, each of a fixed duration specified by the `timespan` argument or a maximum size
   * specified by the `count` argument (which ever is reached first). When the source Observable completes
   * or encounters an error, the current buffer is emitted and the event is propagated.
   *
   * @param timespan
   *            The period of time each buffer is collecting values before it should be emitted, and
   *            replaced with a new buffer.
   * @param count
   *            The maximum size of each buffer before it should be emitted.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces connected non-overlapping buffers which are emitted after
   *         a fixed duration or when the buffer has reached maximum capacity (which ever occurs first).
   */
  def buffer(timespan: Duration, count: Int): Observable[Seq[T]] = {
    val oJava: rx.Observable[_ <: java.util.List[_]] = obs.asJavaObservable.buffer(timespan.length, timespan.unit, count)
    Observable.jObsOfListToScObsOfSeq(oJava.asInstanceOf[rx.Observable[_ <: java.util.List[T]]])
  }

  /**
   * Creates an Observable which produces buffers of collected values. This Observable produces connected
   * non-overlapping buffers, each of a fixed duration specified by the `timespan` argument or a maximum size
   * specified by the `count` argument (which ever is reached first). When the source Observable completes
   * or encounters an error, the current buffer is emitted and the event is propagated.
   *
   * @param timespan
   *            The period of time each buffer is collecting values before it should be emitted, and
   *            replaced with a new buffer.
   * @param count
   *            The maximum size of each buffer before it should be emitted.
   * @param scheduler
   *            The [[rx.lang.scala.Scheduler]] to use when determining the end and start of a buffer.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces connected non-overlapping buffers which are emitted after
   *         a fixed duration or when the buffer has reached maximum capacity (which ever occurs first).
   */
  def buffer(timespan: Duration, count: Int, scheduler: Scheduler): Observable[Seq[T]] = {
    val oJava: rx.Observable[_ <: java.util.List[_]] = obs.asJavaObservable.buffer(timespan.length, timespan.unit, count, scheduler)
    Observable.jObsOfListToScObsOfSeq(oJava.asInstanceOf[rx.Observable[_ <: java.util.List[T]]])
  }

  /**
   * Creates an Observable which produces buffers of collected values. This Observable starts a new buffer
   * periodically, which is determined by the `timeshift` argument. Each buffer is emitted after a fixed timespan
   * specified by the `timespan` argument. When the source Observable completes or encounters an error, the
   * current buffer is emitted and the event is propagated.
   *
   * @param timespan
   *            The period of time each buffer is collecting values before it should be emitted.
   * @param timeshift
   *            The period of time after which a new buffer will be created.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces new buffers periodically, and these are emitted after
   *         a fixed timespan has elapsed.
   */
  def buffer(timespan: Duration, timeshift: Duration): Observable[Seq[T]] = {
    val span: Long = timespan.length
    val shift: Long = timespan.unit.convert(timeshift.length, timeshift.unit)
    val unit: TimeUnit = timespan.unit
    val oJava: rx.Observable[_ <: java.util.List[_]] = obs.asJavaObservable.buffer(span, shift, unit)
    Observable.jObsOfListToScObsOfSeq(oJava.asInstanceOf[rx.Observable[_ <: java.util.List[T]]])
  }

  /**
   * Creates an Observable which produces buffers of collected values. This Observable starts a new buffer
   * periodically, which is determined by the `timeshift` argument. Each buffer is emitted after a fixed timespan
   * specified by the `timespan` argument. When the source Observable completes or encounters an error, the
   * current buffer is emitted and the event is propagated.
   *
   * @param timespan
   *            The period of time each buffer is collecting values before it should be emitted.
   * @param timeshift
   *            The period of time after which a new buffer will be created.
   * @param scheduler
   *            The [[rx.lang.scala.Scheduler]] to use when determining the end and start of a buffer.
   * @return
   *         An [[rx.lang.scala.Observable]] which produces new buffers periodically, and these are emitted after
   *         a fixed timespan has elapsed.
   */
  def buffer(timespan: Duration, timeshift: Duration, scheduler: Scheduler): Observable[Seq[T]] = {
    val span: Long = timespan.length
    val shift: Long = timespan.unit.convert(timeshift.length, timeshift.unit)
    val unit: TimeUnit = timespan.unit
    val oJava: rx.Observable[_ <: java.util.List[_]] = obs.asJavaObservable.buffer(span, shift, unit, scheduler)
    Observable.jObsOfListToScObsOfSeq(oJava.asInstanceOf[rx.Observable[_ <: java.util.List[T]]])
  }

}