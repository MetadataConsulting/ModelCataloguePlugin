package org.modelcatalogue.core.elasticsearch.rx;

import org.elasticsearch.action.ActionListener;
import rx.Subscriber;

class ActionListenerAdapter<T> implements ActionListener<T> {

    private final Subscriber<? super T> subscriber;

    ActionListenerAdapter(Subscriber<? super T> subscriber) {
        this.subscriber = subscriber;
        this.subscriber.onStart();
    }

    @Override
    public void onResponse(T response) {
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public void onFailure(Throwable e) {
        subscriber.onError(e);
    }

}
