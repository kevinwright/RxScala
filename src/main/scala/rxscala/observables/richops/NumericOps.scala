package rxscala
package observables
package richops

class NumericOps[T](val obs: Observable[T]) extends AnyVal {
  /**
   * Returns an Observable that sums up the elements of this Observable.
   *
   * This operation is only available if the elements of this Observable are numbers, otherwise
   * you will get a compilation error.
   *
   * @return an Observable emitting the sum of all the elements of the source Observable
   *         as its single item.
   *
   * @usecase def sum: Observable[T]
   *   @inheritdoc
   */
  def sum[U >: T](implicit num: Numeric[U]): Observable[U] = {
    obs.foldLeft(num.zero)(num.plus)
  }

  /**
   * Returns an Observable that multiplies up the elements of this Observable.
   *
   * This operation is only available if the elements of this Observable are numbers, otherwise
   * you will get a compilation error.
   *
   * @return an Observable emitting the product of all the elements of the source Observable
   *         as its single item.
   *
   * @usecase def product: Observable[T]
   *   @inheritdoc
   */
  def product[U >: T](implicit num: Numeric[U]): Observable[U] = {
    obs.foldLeft(num.one)(num.times)
  }
}