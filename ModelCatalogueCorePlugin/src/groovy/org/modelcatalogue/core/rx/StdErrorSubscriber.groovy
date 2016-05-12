package org.modelcatalogue.core.rx

import rx.Subscriber

class StdErrorSubscriber<T> extends Subscriber<T> {

    static <T> StdErrorSubscriber<T> create(String onErrorMessage) {
        return new StdErrorSubscriber<T>(onErrorMessage)
    }

    private final String onErrorMessage

    StdErrorSubscriber(String onErrorMessage) {
        this.onErrorMessage = onErrorMessage
    }

    @Override
    void onCompleted() {}

    @Override
    void onError(Throwable e) {
        System.err.println(onErrorMessage)
        e.printStackTrace()
    }

    @Override
    void onNext(T aBoolean) {}

}
