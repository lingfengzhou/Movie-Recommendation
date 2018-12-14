package com.csye7200.model.backend

import scala.collection.mutable

trait Cache[K, V] extends (K => V) {

  case class ValueWrapper(value: V, expireTime: Long)

  val lookup: K => V

  def empty: Unit
}

case class ExpirableCache[K, V](lookup: K => V, expireDuration: Long = 86400000) extends Cache[K, V] {

  private def put(k: K, v: V): Unit = cache += (
    (k, ValueWrapper(v, System.currentTimeMillis() + expireDuration))
    )

  override def apply(k: K): V = {
    if (cache.contains(k)) {
      val vw = cache(k)
      if (isExpire(vw)) {
        expire(k)
        val v = lookup(k)
        put(k, v)
        v
      } else {
        vw.value
      }
    } else {
      val v = lookup(k)
      put(k, v)
      v
    }
  }

  private def isExpire(vw: ValueWrapper) = vw.expireTime < System.currentTimeMillis()

  private def expire(k: K) = cache.-=(k)

  val cache: mutable.Map[K, ValueWrapper] = mutable.Map.empty

  def empty: Unit = cache.empty
}

//object Caches {
//  val directorCache = ExpirableCache[String, ???]
//}