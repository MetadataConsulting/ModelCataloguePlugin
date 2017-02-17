package org.modelcatalogue.core.util.builder

import rx.Observer

/**
 * Subprocess monitor sends the notification to upstream but doesn't trigger failure or competition
 * of the upstream monitor.
 */
class SubprocessMonitor implements Observer<String> {

    private final Observer<String> parentMonitor

    SubprocessMonitor(Observer<String> parentMonitor) {
        this.parentMonitor = parentMonitor
    }

    @Override
    void onCompleted() {
        // ignored
    }

    @Override
    void onError(Throwable throwable) {
        parentMonitor.onNext(String.valueOf(throwable))
    }

    @Override
    void onNext(String s) {
        parentMonitor.onNext(s)
    }
}
