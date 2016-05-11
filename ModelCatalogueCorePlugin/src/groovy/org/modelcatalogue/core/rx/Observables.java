package org.modelcatalogue.core.rx;

import grails.gorm.DetachedCriteria;
import rx.Observable;
import rx.Subscriber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Observables {

    private static final int PAGE = 10;
    private static final String ARGUMENT_MAX = "max";
    private static final String ARGUMENT_OFFSET = "offset";

    private Observables(){}

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
    public static <T> Observable<T> observe(DetachedCriteria<T> criteria) {
        return Observable.create(new DetachedCriteriaOnSubscribe<T>(criteria));
    }

    private static class DetachedCriteriaOnSubscribe<T> implements Observable.OnSubscribe<T> {
        private final DetachedCriteria<T> criteria;

        DetachedCriteriaOnSubscribe(DetachedCriteria<T> criteria) {
            this.criteria = criteria;
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
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(ARGUMENT_MAX, PAGE);

            int currentOffset = 0;
            List<T> result;
            do {
                subscriber.onStart();
                result = criteria.list(parameters);
                for (T item : result) {
                    subscriber.onNext(item);
                }
                currentOffset += PAGE;
                parameters.put(ARGUMENT_OFFSET, currentOffset);
            } while (result.size() == PAGE && !subscriber.isUnsubscribed());
            subscriber.onCompleted();
        }
    }
}
