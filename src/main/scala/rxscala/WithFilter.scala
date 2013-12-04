/**
 * Copyright 2013 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rxscala

import ImplicitFunctionConversions.scalaBooleanFunction1ToRxBooleanFunc1
import ImplicitFunctionConversions.scalaFunction1ToRxFunc1

// Cannot yet have inner class because of this error message:
// "implementation restriction: nested class is not allowed in value class.
// This restriction is planned to be removed in subsequent releases."
private[rxscala] class WithFilter[+T] (p: T => Boolean, asJava: rx.Observable[_ <: T]) {

  import ImplicitFunctionConversions._

  def map[B](f: T => B): Observable[B] = {
    Observable[B](asJava.filter(p).map[B](f))
  }

  def flatMap[B](f: T => Observable[B]): Observable[B] = {
    Observable[B](asJava.filter(p).flatMap[B]((x: T) => f(x).asJavaObservable))
  }

  def withFilter(q: T => Boolean): Observable[T] = {
    Observable[T](asJava.filter((x: T) => p(x) && q(x)))
  }

  // there is no foreach here, that's only available on BlockingObservable
}
