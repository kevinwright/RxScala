package rxscala
package observables
package richops

object `package` {
  import scala.language.implicitConversions
  
  @inline implicit def aggregatingOps[T](obs: Observable[T]) = new AggregatingOps(obs)
  @inline implicit def bufferingOps[T](obs: Observable[T]) = new BufferingOps(obs)
  @inline implicit def combinginOps[T](obs: Observable[T]) = new CombiningOps(obs)
  @inline implicit def connectableOps[T](obs: Observable[T]) = new ConnectableOps(obs)
  @inline implicit def errorHandlingOps[T](obs: Observable[T]) = new ErrorHandlingOps(obs)
  @inline implicit def materializeOps[T](obs: Observable[T]) = new MaterializeOps(obs)
  @inline implicit def numericOps[T](obs: Observable[T]) = new NumericOps(obs)
  @inline implicit def partitioningOps[T](obs: Observable[T]) = new PartitioningOps(obs)
  @inline implicit def throttlingOps[T](obs: Observable[T]) = new ThrottlingOps(obs)
  @inline implicit def windowingOps[T](obs: Observable[T]) = new WindowingOps(obs)
}