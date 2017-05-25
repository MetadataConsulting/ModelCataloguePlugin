package org.modelcatalogue.core.elasticsearch.rx;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionResponse;
import rx.Observable;
import rx.functions.Func1;

import java.util.concurrent.Callable;

/**
 * Unility service to simplify working with Rx
 */
public class RxElastic {
    public RxElastic() {}

    /**
     * Creates new observable from given request builder.
     *
     * @param requestBuilder   elasticsearch request builder
     * @param <Request>        the type of the request
     * @param <Response>       the type of the response
     * @param <RequestBuilder> the type of the request builder
     * @return new observable from given request builder
     */
    public static <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> Observable<Response> from(final ActionRequestBuilder<Request, Response, RequestBuilder> requestBuilder) {
        return Observable.create(new ActionRequestBuilderAdapter<Request, Response, RequestBuilder>(requestBuilder));
    }

    /**
     * Creates new observable from given request builder factory.
     *
     * This guaranties that the builder will be recreated each time exception happens and retry is requested.
     *
     * @param factory          elasticsearch request builder factory
     * @param <Request>        the type of the request
     * @param <Response>       the type of the response
     * @param <RequestBuilder> the type of the request builder
     * @return new observable from given request builder
     */
    public static <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> Observable<Response> from(final Callable<ActionRequestBuilder<Request, Response, RequestBuilder>> factory) {
        return Observable.fromCallable(factory).flatMap(new Func1<ActionRequestBuilder<Request, Response, RequestBuilder>, Observable<Response>>() {
            @Override
            public Observable<Response> call(ActionRequestBuilder<Request, Response, RequestBuilder> builder) {
                return RxElastic.from(builder);
            }
        });
    }

    public static final int DEFAULT_RETRIES = 10;
    public static final int DEFAULT_DELAY = 1000;
}
