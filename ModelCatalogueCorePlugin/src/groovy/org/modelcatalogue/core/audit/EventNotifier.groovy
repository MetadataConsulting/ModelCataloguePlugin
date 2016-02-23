package org.modelcatalogue.core.audit

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.RemovalListener
import com.google.common.cache.RemovalNotification
import grails.converters.JSON
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller
import org.springframework.messaging.core.MessageSendingOperations
import rx.Observable
import rx.subjects.PublishSubject
import rx.subjects.Subject

import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

import static rx.Observable.merge

class EventNotifier extends LoggingAuditor {

    private static final Cache<String, Subject<Map<String, Object>,Map<String, Object>>> DEBOUNCE_CACHE = CacheBuilder
            .newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .removalListener(new RemovalListener<String, Subject<Map<String, Object>,Map<String, Object>>>() {
                @Override
                void onRemoval(RemovalNotification<String, Subject<Map<String, Object>,Map<String, Object>>> notification) {
                    notification.value.onCompleted()
                }
            })
            .build()

    final ExecutorService executorService
    final MessageSendingOperations brokerMessagingTemplate

    EventNotifier(MessageSendingOperations brokerMessagingTemplate, ExecutorService executorService) {
        this.brokerMessagingTemplate = brokerMessagingTemplate
        this.executorService = executorService
    }

    @Override
    protected Observable<Long> logChange(Map<String, Object> changeProps, CatalogueElement element, boolean async) {
        // this is always async
        String key = "${GrailsNameUtils.getPropertyName(HibernateHelper.getEntityClass(element))}/${element.getId()}"
        String change = changeProps.type?.toString()
        String elementAsJSON = render(element)
        Subject<Map<String, Object>,Map<String, Object>> debounceQueue = DEBOUNCE_CACHE.getIfPresent(key)

        if (!debounceQueue) {
            debounceQueue = PublishSubject.create()

            debounceQueue.debounce(1, TimeUnit.SECONDS).subscribe({
                brokerMessagingTemplate.convertAndSend("/topic/changes/$key".toString(), it)
            } , {
                throw new RuntimeException("Problems sending element: $element with change props: $changeProps to /topic/changes/$key", it)
            })

            DEBOUNCE_CACHE.put(key, debounceQueue)
        }
        executorService.execute {
            debounceQueue.onNext([element: elementAsJSON, change: change])
        }
        // does not create any entity so it does not emit any id
        return Observable.empty()
    }

    private static String render(CatalogueElement element) {
        StringWriter sw = new StringWriter()
        new JSON(element).render(sw)
        return sw.toString()
    }
}
