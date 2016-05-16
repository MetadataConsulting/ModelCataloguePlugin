package org.modelcatalogue.core.rx;

import grails.gorm.DetachedCriteria;
import org.codehaus.groovy.grails.support.PersistenceContextInterceptor;
import rx.Observable;
import rx.Subscriber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DetachedCriteriaOnSubscribe<T> implements Observable.OnSubscribe<T> {

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
    public void call(Subscriber<? super T> subscriber) {
        try {
            callInternal(subscriber);
        } catch (Throwable th) {
            subscriber.onError(th);
        }
    }

    private void callInternal(Subscriber<? super T> subscriber) {
        int currentOffset = 0;
        List<T> result;
        try {
            subscriber.onStart();
            persistenceInterceptor.init();
            do {
                result = criteria.list(new HashMap<String, Object>(parameters));
                for (T item : result) {
                    subscriber.onNext(item);
                }
                currentOffset += PAGE;
                parameters.put(ARGUMENT_OFFSET, currentOffset);
            } while (result.size() == PAGE && !subscriber.isUnsubscribed());
        } finally {
            persistenceInterceptor.flush();
            persistenceInterceptor.destroy();
            subscriber.onCompleted();
        }

    }
}
