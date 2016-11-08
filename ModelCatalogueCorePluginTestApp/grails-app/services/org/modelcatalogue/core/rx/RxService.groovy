package org.modelcatalogue.core.rx

import grails.gorm.DetachedCriteria
import org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin
import org.codehaus.groovy.grails.support.PersistenceContextInterceptor
import org.codehaus.groovy.runtime.StackTraceUtils
import rx.Observable
import rx.Scheduler
import rx.functions.Func1

class RxService {

    static transactional = false


    PersistenceContextInterceptor persistenceInterceptor

    /**
     * Creates new function to be used with Observable#retryWhen method which retries for given times
     * and delays by <code>attempt * attempt retryDelayMillis</code> milliseconds.
     * @param maxRetries maximum number of attempts
     * @param retryDelayMillis initial delay which is increased by power of attempts on each retry
     * @return function to be used with Observable#retryWhen method
     */
    static Func1<Observable<? extends Throwable>, Observable<?>> withDelay(final int maxRetries, final int retryDelayMillis) {
        return new RetryWithDelay(maxRetries, retryDelayMillis, Collections.emptySet());
    }

    /**
     * Creates new function to be used with Observable#retryWhen method which retries for given times
     * and delays by <code>attempt * attempt retryDelayMillis</code> milliseconds.
     * @param maxRetries maximum number of attempts
     * @param retryDelayMillis initial delay which is increased by power of attempts on each retry
     * @param expectedExceptions set of expected exceptions classes for which only exception message is printed
     * @return function to be used with Observable#retryWhen method
     */
    static Func1<Observable<? extends Throwable>, Observable<?>> withDelay(final int maxRetries, final int retryDelayMillis, final Set<Class<? extends Throwable>> expectedExceptions) {
        return new RetryWithDelay(maxRetries, retryDelayMillis, expectedExceptions);
    }

    Scheduler withPersistence(Scheduler delegate) {
        return new WithPersistenceScheduler(persistenceInterceptor, delegate)
    }

    public <T> T withPersistence(Closure<T> c) {
        persistenceInterceptor.init();
        try {
            return c.call();
        } finally {
            persistenceInterceptor.flush();
            persistenceInterceptor.destroy();
        }
    }


    /**
     * Creates new observables over DetachedCriteria results.
     *
     * Needs to be subscribed on thread with hibernate session bound e.g.
     * <code>Observables.observer(criteria).subscribeOn(executorService)</code>
     *
     * @param criteria criteria to observe
     * @param <T> the type emitted
     * @return the observable over all items of criteria result
     */
    public <T> Observable<T> from(DetachedCriteria<T> criteria, boolean buffer = true, int batchSize = 10, long batchDelay = 0) {
        return from(Collections.emptyMap(), criteria, buffer, batchSize, batchDelay)
    }

    /**
     * Creates new observables over DetachedCriteria results.
     *
     * Needs to be subscribed on thread with hibernate session bound e.g.
     * <code>Observables.observer(criteria).subscribeOn(executorService)</code>
     *
     * @param parameters additional parameters such as sort or order, pagining related parameters such as max or offset are ignored
     * @param criteria criteria to observe
     * @param <T> the type emitted
     * @return the observable over all items of criteria result
     */
    public <T> Observable<T> from(Map<String, Object> parameters, DetachedCriteria<T> criteria, boolean buffer = true, int batchSize = 10, long batchDelay = 0) {
        Observable<T> ret = Observable
            .create(new DetachedCriteriaOnSubscribe<T>(persistenceInterceptor, criteria, parameters))
            .doOnError {
                StackTraceUtils.deepSanitize(it)
                log.warn "Error emitting from detached criteria", it
            }
            .lift(batch(batchSize, batchDelay))

        if (buffer) {
            ret = ret.onBackpressureBuffer()
        }

        return ret
    }

    static <T> Observable.Operator<T,T> batch(int size) {
        return new BatchOperator<T>(size)
    }

    static <T> Observable.Operator<T,T> batch(int size, long batchDelay) {
        return new BatchOperator<T>(size, batchDelay)
    }

    static void clearPropertyInstanceMap() {
        DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP.get().clear()
    }


}
