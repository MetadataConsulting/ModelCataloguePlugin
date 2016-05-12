package org.modelcatalogue.core.rx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import rx.Observable;
import rx.functions.Func1;

import java.util.concurrent.TimeUnit;

/**
 * @link http://stackoverflow.com/questions/22066481/rxjava-can-i-use-retry-but-with-delay
 */
class RetryWithDelay implements  Func1<Observable<? extends Throwable>, Observable<?>> {

    private static final Log log = LogFactory.getLog(RetryWithDelay.class);

    private final int maxRetries;
    private final int retryDelayMillis;
    private int retryCount;

    RetryWithDelay(final int maxRetries, final int retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
        this.retryCount = 0;
    }

    @Override
    public Observable<?> call(Observable<? extends Throwable> attempts) {
        return attempts
                .flatMap(new Func1<Throwable, Observable<?>>() {
                    @Override
                    public Observable<?> call(Throwable throwable) {
                        if (++retryCount < maxRetries) {
                            // When this Observable calls onNext, the original
                            // Observable will be retried (i.e. re-subscribed).

                            long delay = Math.round(Math.pow(retryCount, 2)) * retryDelayMillis;

                            if (log.isWarnEnabled()) {
                                log.warn("Problems with stream, retrying for " + retryCount + " time and waiting " + delay + "ms", throwable);
                            }

                            return Observable.timer(delay,
                                    TimeUnit.MILLISECONDS);
                        }

                        // Max retries hit. Just pass the error along.
                        return Observable.error(throwable);
                    }
                });
    }
}
