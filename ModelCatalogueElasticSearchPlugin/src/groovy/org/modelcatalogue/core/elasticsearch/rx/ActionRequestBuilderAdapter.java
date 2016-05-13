package org.modelcatalogue.core.elasticsearch.rx;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionResponse;
import rx.Observable;
import rx.Subscriber;

class ActionRequestBuilderAdapter<Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> implements Observable.OnSubscribe<Response> {

    private final ActionRequestBuilder<Request, Response, RequestBuilder> builder;

    ActionRequestBuilderAdapter(ActionRequestBuilder<Request, Response, RequestBuilder> builder) {
        this.builder = builder;
    }

    @Override
    public void call(Subscriber<? super Response> subscriber) {
        try {
            builder.execute(new ActionListenerAdapter<Response>(subscriber));
        } catch (Throwable th) {
            subscriber.onError(th);
        }
    }
}
