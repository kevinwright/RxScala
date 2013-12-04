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
package rxscala.subscriptions

import rxscala._

object CompositeSubscription {

  /**
   * Creates a [[rx.lang.scala.subscriptions.CompositeSubscription]] from a group of [[rx.lang.scala.Subscription]].
   */
  def apply(subscriptions: Subscription*): CompositeSubscription = {
    new CompositeSubscription(new rx.subscriptions.CompositeSubscription(subscriptions.map(_.asJavaSubscription).toArray : _*))
  }

  /**
   * Creates a [[rx.lang.scala.subscriptions.CompositeSubscription]].
   */
  def apply(): CompositeSubscription = {
    new CompositeSubscription(new rx.subscriptions.CompositeSubscription())
  }

  /**
   * Creates a [[rx.lang.scala.subscriptions.CompositeSubscription]].
   */
  def apply(subscription: rx.subscriptions.CompositeSubscription): CompositeSubscription = {
    new CompositeSubscription(subscription)
  }
}

/**
 * Represents a group of [[rx.lang.scala.Subscription]] that are disposed together.
 */
class CompositeSubscription private[rxscala] (val asJavaSubscription: rx.subscriptions.CompositeSubscription)
  extends Subscription
{
  /**
   * Adds a subscription to the group,
   * or unsubscribes immediately is the [[rx.subscriptions.CompositeSubscription]] is unsubscribed.
   * @param subscription the subscription to be added.
   * @return the [[rx.subscriptions.CompositeSubscription]] itself.
   */
  def +=(subscription: Subscription): this.type = {
    asJavaSubscription.add(subscription.asJavaSubscription)
    this
  }

  /**
   * Removes and unsubscribes a subscription to the group,
   * @param subscription the subscription be removed.
   * @return the [[rx.subscriptions.CompositeSubscription]] itself.
   */
  def -=(subscription: Subscription): this.type = {
    asJavaSubscription.remove(subscription.asJavaSubscription)
    this
  }

  /**
   * Checks whether the subscription has been unsubscribed.
   */
  def isUnsubscribed: Boolean = asJavaSubscription.isUnsubscribed

}
