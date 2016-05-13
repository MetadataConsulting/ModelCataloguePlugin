package org.modelcatalogue.core.rx

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.runtime.StackTraceUtils
import rx.Subscriber

class ErrorSubscriber<T> extends Subscriber<T> {

    Log log = LogFactory.getLog(ErrorSubscriber)

    static <T> ErrorSubscriber<T> create(String onErrorMessage) {
        return new ErrorSubscriber<T>(onErrorMessage)
    }

    private final String onErrorMessage

    ErrorSubscriber(String onErrorMessage) {
        this.onErrorMessage = onErrorMessage ?: "Error occured in the stream"
    }

    @Override
    void onCompleted() {}

    @Override
    void onError(Throwable e) {
        StackTraceUtils.deepSanitize(e)
        log.error(onErrorMessage, e)
    }

    @Override
    void onNext(T aBoolean) {}

}
