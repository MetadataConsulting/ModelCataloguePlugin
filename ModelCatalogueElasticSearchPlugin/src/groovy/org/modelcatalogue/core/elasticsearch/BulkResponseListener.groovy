package org.modelcatalogue.core.elasticsearch

import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.bulk.BulkItemResponse
import org.elasticsearch.action.bulk.BulkResponse
import rx.Subscriber

class BulkResponseListener implements ActionListener<BulkResponse> {

    final Subscriber<BulkResponse> subscriber

    BulkResponseListener(Subscriber<BulkResponse> subscriber) {
        this.subscriber = subscriber
    }

    @Override
    void onResponse(BulkResponse bulkResponse) {
        BulkItemResponse response = bulkResponse.items.find { it.failed }
        if (response) {
            subscriber.onError(response.failure.cause)
            return
        }
        subscriber.onNext(bulkResponse)
        subscriber.onCompleted()
    }

    @Override
    void onFailure(Throwable e) {
        subscriber.onError(e)
    }
}
