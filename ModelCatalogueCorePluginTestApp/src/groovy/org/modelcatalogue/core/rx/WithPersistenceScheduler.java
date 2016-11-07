package org.modelcatalogue.core.rx;

import org.codehaus.groovy.grails.support.PersistenceContextInterceptor;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;

import java.util.concurrent.TimeUnit;

class WithPersistenceScheduler extends Scheduler {

    private final PersistenceContextInterceptor persistenceContextInterceptor;
    private final Scheduler delegate;

    WithPersistenceScheduler(PersistenceContextInterceptor persistenceContextInterceptor, Scheduler delegate) {
        this.persistenceContextInterceptor = persistenceContextInterceptor;
        this.delegate = delegate;
    }

    @Override
    public Worker createWorker() {
        return new WithPersistenceWorker(delegate.createWorker());
    }

    private class WithPersistenceAction0 implements Action0 {

        private final Action0 delegate;

        WithPersistenceAction0(Action0 delegate) {
            this.delegate = delegate;
        }

        @Override
        public void call() {
            persistenceContextInterceptor.init();
            try {
                delegate.call();
            } finally {
                persistenceContextInterceptor.flush();
                persistenceContextInterceptor.destroy();
            }
        }
    }

    private class WithPersistenceWorker extends Scheduler.Worker {

        private final Worker delegate;

        WithPersistenceWorker(Worker delegate) {
            this.delegate = delegate;
        }

        @Override
        public Subscription schedule(Action0 action) {
            return delegate.schedule(new WithPersistenceAction0(action));
        }

        @Override
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            return delegate.schedule(new WithPersistenceAction0(action), delayTime, unit);
        }

        @Override
        public void unsubscribe() {
            delegate.unsubscribe();
        }

        @Override
        public boolean isUnsubscribed() {
            return delegate.isUnsubscribed();
        }
    }
}
