package org.modelcatalogue.core.rx;

import rx.Observable;
import rx.Producer;
import rx.Subscriber;

import java.util.concurrent.atomic.AtomicLong;

/**
 * If the {@link Observable} supports pulling it will issue {@link Subscriber#request(long)} every
 * <code>batchSize</code> items instructing the {@link Observable} to emit items in particular batches.
 * @param <T> the type of the items emitted
 */
public class BatchOperator<T> implements Observable.Operator<T, T> {


    private final long batchSize;

    public BatchOperator(long batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public Subscriber<? super T> call(Subscriber<? super T> subscriber) {
        return new BatchSubscriber<T>(subscriber, batchSize);
    }

    private static final class BatchSubscriber<T> extends Subscriber<T> {

        private final Subscriber<? super T> child;
        private final long by;
        private final AtomicLong processed = new AtomicLong();

        BatchSubscriber(Subscriber<? super T> child, long by) {
            this.child = child;
            this.by = by;
        }

        @Override
        public void onCompleted() {
            child.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            child.onError(e);
        }

        @Override
        public void onNext(T t) {
            if (processed.incrementAndGet() == by) {
                request(by);
                processed.set(0);
            }
            child.onNext(t);
        }

        @Override
        public void onStart() {
            super.onStart();
            request(by);
            child.onStart();
        }

        @Override
        public void setProducer(Producer p) {
            super.setProducer(p);
            child.setProducer(p);
        }
    }

}
