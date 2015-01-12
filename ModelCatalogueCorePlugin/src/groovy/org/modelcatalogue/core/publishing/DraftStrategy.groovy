package org.modelcatalogue.core.publishing

import groovy.transform.PackageScope

/**
 * Created by ladin on 09.01.15.
 */
class DraftStrategy {

    @PackageScope final boolean forceNew
    @PackageScope final boolean copyRelationships

    private DraftStrategy(boolean forceNew, boolean copyRelationships) {
        this.forceNew = forceNew
        this.copyRelationships = copyRelationships
    }
    static DraftStrategy userFriendly() {
        new DraftStrategy(true, true)
    }

    static DraftStrategy importFriendly() {
        new DraftStrategy(false, false)
    }
}
