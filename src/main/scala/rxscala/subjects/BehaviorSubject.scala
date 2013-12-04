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
package rxscala.subjects

object BehaviorSubject {
  def apply[T](value: T): BehaviorSubject[T] = {
    new BehaviorSubject[T](rx.subjects.BehaviorSubject.createWithDefaultValue(value))
  }
}

class BehaviorSubject[T] private[rxscala] (val asJavaSubject: rx.subjects.BehaviorSubject[T]) extends Subject[T,T]  {}




