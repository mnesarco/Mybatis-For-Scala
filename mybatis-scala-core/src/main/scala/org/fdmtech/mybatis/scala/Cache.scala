/*
 * Copyright 2011 Frank D. Martinez M. [mnesarco at gmail.com].
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fdmtech.mybatis.scala

/** Mybatis cache supported eviction policies.
 *
 *  - LRU (Least Recently Used): Removes objects that haven’t been used for the longst period of time.
 *  - FIFO (First In First Out): Removes objects in the order that they entered the cache.
 *  - SOFT (Soft Reference): Removes objects based on the garbage collector state and the rules of Soft References.
 *  - WEAK (Weak Reference): More aggressively removes objects based on the garbage collector state and rules of Weak References.
 *
 *  The default is LRU.
 */
object CacheEviction extends Enumeration {
  val LRU, FIFO, SOFT, WEAK = Value
}

/** By default, there is no caching enabled, except for local session caching,
 *  which improves performance and is required to resolve circular dependencies.
 *  To enable a second level of caching, you simply need to add an instance
 *  of this class to the builder.
 *
 *  @param eviction eviction policy, [[org.fdmtech.mybatis.scala.mapping.CacheEviction]]
 *  @param flushInterval can be set to any positive integer and should represent a reasonable
 *    amount of time specified in milliseconds. The default is not set,
 *    thus no flush interval is used and the cache is only flushed by calls to statements.
 *  @param size can be set to any positive integer, keep in mind the size of the objects your
 *    caching and the available memory resources of your environment. The default is 1024.
 *  @param readOnly attribute can be set to true or false. A read-only cache will return
 *    the same instance of the cached object to all callers. Thus such objects should not be modified.
 *    This offers a significant performance advantage though.
 *    A read-write cache will return a copy (via serialization) of the cached object.
 *    This is slower, but safer, and thus the default is false.
 */
case class Cache(
   val eviction : CacheEviction = CacheEviction.LRU,
   val flushInterval : Int = -1,
   val size : Int = 1024,
   val readOnly : Boolean = false,
   var owner : String = null)

