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
package subjects

import rxscala.observables.Observable

/**
* A Subject is an Observable and an Observer at the same time.
*/
trait Subject[-T, +R] extends Observable[R] with Observer[T] {
  val asJavaSubject: rx.subjects.Subject[_ >: T, _<: R]
  def asJavaObservable: rx.Observable[_ <: R] = asJavaSubject
  def asJavaObserver: rx.Observer[_ >: T] = asJavaSubject
}

