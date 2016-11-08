package org.modelcatalogue.core.rx;

import grails.gorm.DetachedCriteria;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.grails.support.PersistenceContextInterceptor;
import rx.Observable;
import rx.Producer;
import rx.Subscriber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class DetachedCriteriaOnSubscribe<T> implements Observable.OnSubscribe<T> {

    private static final Log log = LogFactory.getLog(DetachedCriteriaOnSubscribe.class);
    private static final int PAGE = 10;
    private static final String ARGUMENT_MAX = "max";
    private static final String ARGUMENT_OFFSET = "offset";

    private final PersistenceContextInterceptor persistenceInterceptor;
    private final DetachedCriteria<T> criteria;
    private final Map<String, Object> parameters;

    public DetachedCriteriaOnSubscribe(PersistenceContextInterceptor persistenceInterceptor, DetachedCriteria<T> criteria, Map<String, Object> parameters) {
        this.criteria = criteria;
        this.persistenceInterceptor = persistenceInterceptor;
        this.parameters = new HashMap<String, Object>(parameters);
        this.parameters.put(ARGUMENT_MAX, PAGE);
    }

    @Override
    public void call(final Subscriber<? super T> subscriber) {
        subscriber.setProducer(new Producer() {

            private final AtomicInteger offset = new AtomicInteger(0);
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public void request(long n) {
                if (log.isDebugEnabled()) {
                    log.debug("offset: " + offset.get() + ", requested: "  + n + ",  criteria " + criteria.hashCode() + " - " + criteria.getCriteria());
                }
                if (n <= 0) {
                    // just reset the offset
                    offset.set(0);
                    return;
                }

                try {
                    persistenceInterceptor.init();
                    emitAnotherPage(n);
                } catch (Throwable th){
                    subscriber.onError(th);
                } finally {
                    persistenceInterceptor.flush();
                    persistenceInterceptor.destroy();
                }
            }

            private void emitAnotherPage(long n) {
                if (n < Integer.MAX_VALUE) {
                    parameters.put(ARGUMENT_MAX, (int) n);
                    parameters.put(ARGUMENT_OFFSET, offset.getAndAdd((int)n));
                } else {
                    parameters.put(ARGUMENT_MAX, PAGE);
                    parameters.put(ARGUMENT_OFFSET, offset.getAndAdd(PAGE));
                }
                List<T> results = criteria.list(new HashMap<String, Object>(parameters));
                for (T item : results) {
                    subscriber.onNext(item);
                }

                if (counter.incrementAndGet() == 10) {
                    cleanUpGorm();
                    counter.set(0);
                }

                if (results.size() < n) {
                    subscriber.onCompleted();
                    return;
                }
                if (n >= Integer.MAX_VALUE) {
                    emitAnotherPage(n);
                }
            }

        });
        subscriber.onStart();
    }

    private void cleanUpGorm() {
        persistenceInterceptor.flush();
        persistenceInterceptor.clear();
        RxService.clearPropertyInstanceMap();
    }
}
