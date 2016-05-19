package org.modelcatalogue.core.util.builder

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.StackTraceUtils
import org.springframework.messaging.core.MessageSendingOperations
import rx.subjects.PublishSubject

import java.util.concurrent.TimeUnit

@CompileStatic
class BuildProgressMonitor implements Serializable, ProgressMonitor {

    private static MessageSendingOperations brokerMessagingTemplate

    static BuildProgressMonitor create(String name, key) {
        BuildProgressMonitor monitor = new BuildProgressMonitor(name, key?.toString())
        MONITORS_CACHE.put(key?.toString(), monitor)
        monitor
    }

    static BuildProgressMonitor get(key) {
        MONITORS_CACHE.getIfPresent(key?.toString())
    }

    static BuildProgressMonitor remove(key) {
        BuildProgressMonitor monitor = MONITORS_CACHE.getIfPresent(key?.toString())
        MONITORS_CACHE.invalidate(key?.toString())
        return monitor
    }

    private static final Cache<String, BuildProgressMonitor> MONITORS_CACHE = CacheBuilder.newBuilder().initialCapacity(20).expireAfterAccess(1, TimeUnit.DAYS).build()

    private final String name
    private final String key
    private final StringBuffer buffer
    private PublishSubject<String> queue = PublishSubject.create()

    BuildProgressMonitor(String name, String key) {
        this.name = name
        this.key = key
        this.buffer = new StringBuffer()
        if (!brokerMessagingTemplate) {
            brokerMessagingTemplate = Holders.applicationContext.getBean('brokerMessagingTemplate', MessageSendingOperations)
        }
        queue
            .buffer(300, TimeUnit.MILLISECONDS)
            .doOnError {
                brokerMessagingTemplate.convertAndSend("/topic/feedback/$key/lines".toString(), [lines: printException(it).toString()])
            }
            .subscribe {
                if (!it) {
                    return
                }
                brokerMessagingTemplate.convertAndSend("/topic/feedback/$key/lines".toString(), [lines: it.join('\n')])
            }
    }


    void onNext(String message) {
        buffer << message << '\n'
        queue.onNext(message)
    }

    void onCompleted() {
        queue.onCompleted()
    }

    @Override
    String getName() {
        return name
    }

    @Override
    void onError(Throwable th) {
        StringWriter sw = printException(th)
        onNext(sw.toString())
        queue.onError(th)
    }

    private static StringWriter printException(Throwable th) {
        StackTraceUtils.deepSanitize(th)
        StringWriter sw = new StringWriter()
        PrintWriter pw = new PrintWriter(sw)
        th.print(pw)
        sw
    }

    String getLastMessages() {
        buffer.toString()
    }

}


