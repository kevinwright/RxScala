package rxscala
package observables
package richops

class MaterializeOps[T](val obs: Observable[T]) extends AnyVal {

  /**
   * Turns all of the notifications from a source Observable into [[rx.lang.scala.Observer.onNext onNext]] emissions,
   * and marks them with their original notification types within [[rx.lang.scala.Notification]] objects.
   *
   * <img width="640" src="https://raw.github.com/wiki/Netflix/RxJava/images/rx-operators/materialize.png">
   *
   * @return an Observable whose items are the result of materializing the items and
   *         notifications of the source Observable
   */
  def materialize: Observable[Notification[T]] = {
    Observable[rx.Notification[_ <: T]](obs.asJavaObservable.materialize()).map(Notification wrap _)
  }
  
  /**
   * Returns an Observable that reverses the effect of [[rx.lang.scala.Observable.materialize]] by
   * transforming the [[rx.lang.scala.Notification]] objects emitted by the source Observable into the items
   * or notifications they represent.
   *
   * This operation is only available if `this` is of type `Observable[Notification[U]]` for some `U`, 
   * otherwise you will get a compilation error.
   *
   * <img width="640" src="https://github.com/Netflix/RxJava/wiki/images/rx-operators/dematerialize.png">
   *
   * @return an Observable that emits the items and notifications embedded in the [[rx.lang.scala.Notification]] objects emitted by the source Observable
   *
   * @usecase def dematerialize[U]: Observable[U]
   *   @inheritdoc
   *
   */
  // with =:= it does not work, why?
  def dematerialize[U](implicit evidence: Observable[T] <:< Observable[Notification[U]]): Observable[U] = {
    val o1: Observable[Notification[U]] = obs
    val o2: Observable[rx.Notification[_ <: U]] = o1.map(_.asJava)
    val o3 = o2.asJavaObservable.dematerialize[U]()
    Observable[U](o3)
  }
}