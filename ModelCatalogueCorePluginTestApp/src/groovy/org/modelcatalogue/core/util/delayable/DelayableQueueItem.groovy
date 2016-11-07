package org.modelcatalogue.core.util.delayable

import groovy.transform.PackageScope

@PackageScope class DelayableQueueItem {
    final String methodName
    final Object[] args

    DelayableQueueItem(String methodName, Object[] args) {
        this.methodName = methodName
        this.args = args
    }
}
