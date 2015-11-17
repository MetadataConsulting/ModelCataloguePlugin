package org.modelcatalogue.core.elasticsearch

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.collect.ImmutableMap
import org.hibernate.proxy.HibernateProxyHelper
import rx.Observable

class IndexingSession {

    final Cache<String, Document> documentCache = CacheBuilder.newBuilder().build()
    final Cache<String, Observable<Boolean>> indexingCache = CacheBuilder.newBuilder().build()

    static IndexingSession create() {
        return new IndexingSession()
    }

    private IndexingSession() {}

    Document getDocument(Object o) {
        documentCache.get("${HibernateProxyHelper.getClassWithoutInitializingProxy(o)}:${o.getId()}") {
            createDocument(o)
        }
    }

    Observable<Boolean> indexOnlyOnce(Object o, Closure<Observable<Boolean>> index) {
        indexingCache.get("${HibernateProxyHelper.getClassWithoutInitializingProxy(o)}:${o.getId()}") {
            index().cache()
        }
    }

    private Document createDocument(Object object) {
        if (!object) {
            return new Document('','',ImmutableMap.of())
        }

        if (object instanceof Document) {
            return object
        }

        ImmutableMap<String, Object> result = DocumentSerializer.Registry.get(object.class).buildDocument(this, object, ImmutableMap.builder()).build()

        if (result._id) {
            throw new IllegalArgumentException("Payload for $object cannot contain _id")
        }

        if (result._type) {
            throw new IllegalArgumentException("Payload for $object cannot contain _type")
        }

        new Document(ElasticSearchService.getTypeName(HibernateProxyHelper.getClassWithoutInitializingProxy(object)), object.getId()?.toString(), result)
    }
}
