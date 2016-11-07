package org.modelcatalogue.core.util.builder;

import rx.Observer;

public interface ProgressMonitor extends Observer<String> {

    String getName();
    String getLastMessages();

    ProgressMonitor NOOP = new ProgressMonitor() {
        @Override public void onNext(String message) {}
        @Override public void onCompleted() {}
        @Override public void onError(Throwable th) {}
        @Override public String getName() { return  "Feedback Not Implemented"; }
        @Override public String getLastMessages() { return  "Not implemented. No progress is currently logged."; }
    };
}
