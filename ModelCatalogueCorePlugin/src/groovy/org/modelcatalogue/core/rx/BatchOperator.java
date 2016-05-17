package org.modelcatalogue.core.rx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import rx.Observable;
import rx.Producer;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * If the {@link Observable} supports pulling it will issue {@link Subscriber#request(long)} every
 * <code>batchSize</code> items instructing the {@link Observable} to emit items in particular batches.
 *
 * Optional <code>delay</code> can be set between each batch exectution.
 *
 * @param <T> the type of the items emitted
 */
public class BatchOperator<T> implements Observable.Operator<T, T> {

    private static final Log log = LogFactory.getLog(BatchOperator.class);

    private final long batchSize;
    private final long delay;
    private Scheduler scheduler = Schedulers.computation();


    public BatchOperator(long batchSize) {
        this(batchSize, 0);
    }

    public BatchOperator(long batchSize, long delay) {
        this.batchSize = batchSize;
        this.delay = delay;
    }

    public BatchOperator<T> withScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    @Override
    public Subscriber<? super T> call(Subscriber<? super T> subscriber) {
        return new BatchSubscriber<T>(subscriber, batchSize, delay, scheduler.createWorker());
    }

    private static final class BatchSubscriber<T> extends Subscriber<T> {

        private final Subscriber<? super T> child;
        private final long by;
        private final long delay;
        private final Scheduler.Worker worker;
        private final AtomicLong processed = new AtomicLong();

        BatchSubscriber(Subscriber<? super T> child, long by, long delay, Scheduler.Worker worker) {
            this.child = child;
            this.by = by;
            this.delay = delay;
            this.worker = worker;
        }

        @Override
        public void onCompleted() {
            if (log.isDebugEnabled()) {
                log.debug(hashCode() + " on completed");
            }
            child.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            if (log.isInfoEnabled()) {
                log.info(hashCode() + " on error", e);
            }
            child.onError(e);
        }

        @Override
        public void onNext(T t) {
            long count = processed.incrementAndGet();
            if (log.isDebugEnabled()) {
                log.debug(hashCode() + " on next: " + t + ", processed: " + count);
            }
            child.onNext(t);
            if (count == by) {
                processed.set(0);
                if (delay > 0) {
                    worker.schedule(new Action0() {
                        @Override
                        public void call() {
                            request(by);
                        }
                    }, delay, TimeUnit.MILLISECONDS);
                } else {
                    request(by);
                }
            }
        }

        @Override
        public void onStart() {
            if (log.isDebugEnabled()) {
                log.debug(hashCode() + " on start");
            }
            super.onStart();
            request(by);
            child.onStart();
        }
    }

}
