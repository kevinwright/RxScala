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

import rxscala._

import java.lang.Exception
import java.{ lang => jlang }
import rx.util.functions._
import scala.collection.Seq
import java.{lang => jlang}
import scala.language.implicitConversions

/**
 * These function conversions convert between Scala functions and Rx `Func`s and `Action`s.
 * Most RxScala users won't need them, but they might be useful if one wants to use
 * the `rx.Observable` directly instead of using `rx.lang.scala.Observable` or if one wants
 * to use a Java library taking/returning `Func`s and `Action`s.
 */
object ImplicitFunctionConversions {
  import language.implicitConversions

  implicit def schedulerActionToFunc2[T](action: (Scheduler, T) => Subscription) =
    new Func2[rx.Scheduler, T, rx.Subscription] {
      def call(s: rx.Scheduler, t: T): rx.Subscription = {
        action(s, t).asJavaSubscription
      }
    }

  implicit def toJavaNotification[T](s: Notification[T]): rx.Notification[_ <: T] = s.asJava
  implicit def toScalaNotification[T](s: rx.Notification[_ <: T]): Notification[T] = Notification.wrap(s)

  implicit def toJavaSubscription(s: Subscription): rx.Subscription = s.asJavaSubscription
  implicit def toScalaSubscription(s: rx.Subscription): Subscription = Subscription(s)

  implicit def scalaSchedulerToJavaScheduler(s: Scheduler): rx.Scheduler = s.asJavaScheduler
  implicit def javaSchedulerToScalaScheduler(s: rx.Scheduler): Scheduler = Scheduler(s)

  implicit def toJavaObserver[T](s: Observer[T]): rx.Observer[_ >: T] = s.asJavaObserver
  implicit def toScalaObserver[T](s: rx.Observer[_ >: T]): Observer[T] = Observer(s)

  implicit def toJavaObservable[T](s: Observable[T]): rx.Observable[_ <: T] = s.asJavaObservable
  implicit def toScalaObservable[T](s: rx.Observable[_ <: T]): Observable[T] = Observable(s)

  implicit def scalaFunction1ToOnSubscribeFunc[T](f: rxscala.Observer[T] => Subscription) =
    new rx.Observable.OnSubscribeFunc[T] {
      def onSubscribe(obs: rx.Observer[_ >: T]): rx.Subscription = {
        f(Observer(obs))
      }
    }

  implicit def scalaByNameParamToFunc0[B](param: => B): Func0[B] =
    new Func0[B] {
      def call(): B = param
    }

  implicit def scalaFunction0ProducingUnitToAction0(f: (() => Unit)): Action0 =
    new Action0 {
      def call(): Unit = f()
    }

  implicit def Action1toScalaFunction1ProducingUnit[A](f: Action1[A]): (A=>Unit) = {
    a => f(a)
  }

  implicit def scalaFunction1ProducingUnitToAction1[A](f: (A => Unit)): Action1[A] =
    new Action1[A] {
      def call(a: A): Unit = f(a)
    }

  implicit def scalaBooleanFunction1ToRxBooleanFunc1[A](f: (A => Boolean)): Func1[A, jlang.Boolean] =
    new Func1[A, jlang.Boolean] {
      def call(a: A): jlang.Boolean = f(a).booleanValue
    }

  implicit def scalaBooleanFunction2ToRxBooleanFunc1[A, B](f: ((A, B) => Boolean)): Func2[A, B, jlang.Boolean] =
    new Func2[A, B, jlang.Boolean] {
      def call(a: A, b: B): jlang.Boolean = f(a, b).booleanValue
    }
  
  implicit def scalaFuncNToRxFuncN[R](f: Seq[java.lang.Object] => R): FuncN[R] =
    new FuncN[R] {
      def call(args: java.lang.Object*): R = f(args)
    }

  implicit def convertTakeWhileFuncToRxFunc2[A](f: (A, Int) => Boolean): Func2[A, jlang.Integer, jlang.Boolean] =
    new Func2[A, jlang.Integer, jlang.Boolean] {
      def call(a: A, b: jlang.Integer): jlang.Boolean = f(a, b).booleanValue
    }

  implicit def convertComparisonFuncToRxFunc2[A](f: (A, A) => Int): Func2[A, A, jlang.Integer] =
    new Func2[A, A, jlang.Integer] {
      def call(a1: A, a2: A): jlang.Integer = f(a1, a2).intValue
    }

  implicit def exceptionFunction1ToRxExceptionFunc1[A <: Exception, B](f: (A => B)): Func1[Exception, B] =
    new Func1[Exception, B] {
      def call(ex: Exception): B = f(ex.asInstanceOf[A])
    }

  implicit def scalaFunction0ToRxFunc0[A](f: () => A): Func0[A] =
    new Func0[A] {
      def call(): A = f()
    }

  implicit def scalaFunction1ToRxFunc1[A, B](f: (A => B)): Func1[A, B] =
    new Func1[A, B] {
      def call(a: A): B = f(a)
    }

  implicit def scalaFunction2ToRxFunc2[A, B, C](f: (A, B) => C): Func2[A, B, C] =
    new Func2[A, B, C] {
      def call(a: A, b: B) = f(a, b)
    }

  implicit def scalaFunction3ToRxFunc3[A, B, C, D](f: (A, B, C) => D): Func3[A, B, C, D] =
    new Func3[A, B, C, D] {
      def call(a: A, b: B, c: C) = f(a, b, c)
    }

  implicit def scalaFunction4ToRxFunc4[A, B, C, D, E](f: (A, B, C, D) => E): Func4[A, B, C, D, E] =
    new Func4[A, B, C, D, E] {
      def call(a: A, b: B, c: C, d: D) = f(a, b, c, d)
    }
}
