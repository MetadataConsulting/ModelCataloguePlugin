package org.modelcatalogue.core.util.builder

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.StackTraceUtils
import org.modelcatalogue.core.cache.CacheService
import org.springframework.messaging.core.MessageSendingOperations
import rx.subjects.PublishSubject

import java.util.concurrent.TimeUnit

@CompileStatic
class BuildProgressMonitor implements Serializable, ProgressMonitor {

    private static MessageSendingOperations brokerMessagingTemplate

    static BuildProgressMonitor create(String name, key) {
        BuildProgressMonitor monitor = new BuildProgressMonitor(name, key?.toString())
        CacheService.MONITORS_CACHE.put(key?.toString(), monitor)
        monitor
    }

    static BuildProgressMonitor get(key) {
        CacheService.MONITORS_CACHE.getIfPresent(key?.toString())
    }

    static BuildProgressMonitor remove(key) {
        BuildProgressMonitor monitor = CacheService.MONITORS_CACHE.getIfPresent(key?.toString())
        CacheService.MONITORS_CACHE.invalidate(key?.toString())
        return monitor
    }


    private final String name
    private final String key
    private final StringBuffer buffer

    private BuildProgressMonitorStatus status = BuildProgressMonitorStatus.RUNNING
    private long lastUpdated = System.currentTimeMillis()
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
        lastUpdated = System.currentTimeMillis()
        buffer << message << '\n'
        queue.onNext(message)
    }

    void onCompleted() {
        lastUpdated = System.currentTimeMillis()
        onNext("\n<strong class='text-success'> JOB $name COMPLETED SUCCESSFULLY</strong>")
        status = BuildProgressMonitorStatus.FINISHED
        queue.onCompleted()
    }

    @Override
    String getName() {
        return name
    }

    @Override
    void onError(Throwable th) {
        lastUpdated = System.currentTimeMillis()
        StringWriter sw = printException(th)
        th.printStackTrace(new PrintWriter(sw))
        onNext(sw.toString())
        status = BuildProgressMonitorStatus.FAILED
        onNext("\n\n<strong class='text-danger'> JOB $name FAILED</strong>\n")
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

    BuildProgressMonitorStatus getStatus() {
        return status
    }

    long getLastUpdated() {
        return lastUpdated
    }
}


