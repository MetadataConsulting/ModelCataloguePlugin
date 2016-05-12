package org.modelcatalogue.core.rx

import grails.gorm.DetachedCriteria
import org.codehaus.groovy.grails.support.PersistenceContextInterceptor
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
        return new RetryWithDelay(maxRetries, retryDelayMillis);
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
    public <T> Observable<T> from(DetachedCriteria<T> criteria) {
        return Observable.create(new DetachedCriteriaOnSubscribe<T>(persistenceInterceptor, criteria))
    }

}
