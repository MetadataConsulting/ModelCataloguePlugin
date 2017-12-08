package org.modelcatalogue.core.audit

import grails.converters.JSON
import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.hibernate.LazyInitializationException
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.cache.CacheService
import org.modelcatalogue.core.util.HibernateHelper
import org.springframework.messaging.core.MessageSendingOperations
import rx.Observable
import rx.subjects.PublishSubject
import rx.subjects.Subject
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

class EventNotifier extends LoggingAuditor {
    final ExecutorService executorService
    final MessageSendingOperations brokerMessagingTemplate

    EventNotifier(MessageSendingOperations brokerMessagingTemplate, ExecutorService executorService) {
        this.brokerMessagingTemplate = brokerMessagingTemplate
        this.executorService = executorService
    }

    @Override
    protected Observable<Long> logChange(Map<String, Object> changeProps, CatalogueElement element, boolean async) {
        // this is always async
        if (!getSystem()) {

            String change = changeProps.type?.toString()
            String elementAsJSON = render(element)
            String key = "${GrailsNameUtils.getPropertyName(HibernateHelper.getEntityClass(element))}/${element.getId()}"
            Subject<Map<String, Object>,Map<String, Object>> debounceQueue = CacheService.DEBOUNCE_CACHE.getIfPresent(key)
            if (!debounceQueue) {
                debounceQueue = PublishSubject.create()

                debounceQueue.debounce(1, TimeUnit.SECONDS).subscribe({
                    brokerMessagingTemplate.convertAndSend("/topic/changes/$key".toString(), it)
                } , {
                    throw new RuntimeException("Problems sending element: ${element} with change props: $changeProps to /topic/changes/$key", it)
                })

                CacheService.DEBOUNCE_CACHE.put(key, debounceQueue)
            }
            executorService.execute {
                debounceQueue.onNext([element: elementAsJSON, change: change])
            }

            // notify the data model that some nested element has been changed
            if (element.dataModel) {
                logChange(element.dataModel, type: ChangeType.DATA_MODEL_CHANGED, async)
            }
        }

        // does not create any entity so it does not emit any id
        return Observable.empty()
    }

    private static String render(CatalogueElement element) {
        element = HibernateHelper.ensureNoProxy(element)
        try {
            StringWriter sw = new StringWriter()
            new JSON(element).render(sw)
            return sw.toString()
        } catch (LazyInitializationException ex){
            return render(element.attach())
        } catch (ConverterException ce) {
            throw new IllegalArgumentException("Unable to convert $element to JSON", ce)
        }
    }
}
