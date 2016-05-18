package org.modelcatalogue.core.elasticsearch.rx;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionResponse;
import rx.Observable;

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

    public static final int DEFAULT_RETRIES = 10;
    public static final int DEFAULT_DELAY = 500;
}
