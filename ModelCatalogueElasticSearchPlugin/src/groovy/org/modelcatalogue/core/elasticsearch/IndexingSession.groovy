package org.modelcatalogue.core.elasticsearch

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.collect.ImmutableMap
import org.hibernate.proxy.HibernateProxyHelper
import rx.Observable

class IndexingSession {

    final Cache<String, Map> documentCache = CacheBuilder.newBuilder().build()
    final Cache<String, Observable<Boolean>> indexingCache = CacheBuilder.newBuilder().build()

    static IndexingSession create() {
        return new IndexingSession()
    }

    private IndexingSession() {}

    Map getDocument(Object o) {
        documentCache.get("${HibernateProxyHelper.getClassWithoutInitializingProxy(o)}:${o.getId()}") {
            createDocument(o)
        }
    }

    Observable<Boolean> indexOnlyOnce(Object o, Closure<Observable<Boolean>> index) {
        indexingCache.get("${HibernateProxyHelper.getClassWithoutInitializingProxy(o)}:${o.getId()}") {
            index().cache()
        }
    }

    private Map createDocument(Object object) {
        if (!object) {
            return [:]
        }

        if (object instanceof Map) {
            return object
        }

        Map result = DocumentSerializer.Registry.get(object.class).buildDocument(this, object, ImmutableMap.builder()).build()

        if (!result._id) {
            throw new IllegalArgumentException("_id field for $object is missing")
        }

        if (!result._type) {
            throw new IllegalArgumentException("_type dfield for $object is missing")
        }

        result
    }
}
