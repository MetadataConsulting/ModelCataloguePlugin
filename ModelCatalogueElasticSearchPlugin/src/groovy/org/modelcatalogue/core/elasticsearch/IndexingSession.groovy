package org.modelcatalogue.core.elasticsearch

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.collect.ImmutableMap
import org.elasticsearch.indices.IndexAlreadyExistsException
import org.elasticsearch.transport.RemoteTransportException
import org.hibernate.proxy.HibernateProxyHelper
import org.modelcatalogue.core.util.HibernateHelper
import rx.Observable

import javax.management.InstanceAlreadyExistsException

import static rx.Observable.just

class IndexingSession {

    final Cache<String, Document> documentCache = CacheBuilder.newBuilder().build()
    final Cache<String, Observable<Boolean>> indexingCache = CacheBuilder.newBuilder().build()

    static IndexingSession create() {
        return new IndexingSession()
    }

    private IndexingSession() {}

    Document getDocument(Object o) {
        if (!o) {
            return new Document('','',ImmutableMap.of())
        }

        try {
            return documentCache.get("${HibernateHelper.getEntityClass(o)}:${o.getId()}") {
                createDocument(o)
            }
        } catch (IllegalStateException ignored) {
            return createDocument(o)
        }
    }

    Observable<Boolean> indexOnlyOnce(Object o, Closure<Observable<Boolean>> index) {
        if (!o) {
            return just(false)
        }
        try {
            return indexingCache.get("${HibernateHelper.getEntityClass(o)}:${o.getId()}") {
                index().cache()
            }
        } catch (IllegalStateException ignored) {
            try {
                return index().cache()
            } catch(RemoteTransportException rte) {
                if (rte.cause instanceof  InstanceAlreadyExistsException) {
                    return just(true)
                }
                throw rte
            }
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

        new Document(ElasticSearchService.getTypeName(HibernateHelper.getEntityClass(object)), object.getId()?.toString(), result)
    }
}
