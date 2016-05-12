package org.modelcatalogue.core.rx

import org.apache.commons.logging.Log
import rx.Subscriber

class LoggingSubscriber<T> extends Subscriber<T> {

    static <T> LoggingSubscriber<T> create(Log log, String onCompletedMessage, String onErrorMessage) {
        return new LoggingSubscriber<T>(log, onCompletedMessage, onErrorMessage)
    }

    private final Log log
    private final String onCompletedMessage
    private final String onErrorMessage

    LoggingSubscriber(Log log, String onCompletedMessage, String onErrorMessage) {
        this.log = log
        this.onCompletedMessage = onCompletedMessage
        this.onErrorMessage = onErrorMessage
    }

    @Override
    void onCompleted() {
        log.info onCompletedMessage
    }

    @Override
    void onError(Throwable e) {
        log.error(onErrorMessage, e)
    }

    @Override
    void onNext(T aBoolean) {}

}
