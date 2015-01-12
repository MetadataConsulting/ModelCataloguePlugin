package org.modelcatalogue.core.publishing

import groovy.transform.PackageScope
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ElementStatus

/**
 * Created by ladin on 09.01.15.
 */
class DraftContext {

    @PackageScope final boolean forceNew
    @PackageScope final boolean copyRelationships

    private Set<Runnable> pendingRelationshipsTasks = new LinkedHashSet<Runnable>()

    private DraftContext(boolean forceNew, boolean copyRelationships) {
        this.forceNew = forceNew
        this.copyRelationships = copyRelationships
    }
    static DraftContext userFriendly() {
        new DraftContext(true, true)
    }

    static DraftContext importFriendly() {
        new DraftContext(false, false)
    }

    void delayRelationshipCopying(CatalogueElement draft, CatalogueElement oldVersion) {
        if (copyRelationships) {
            pendingRelationshipsTasks << new CopyAssociationsAndRelationships(draft, oldVersion)
        }
    }

    void resolvePendingRelationships() {
        pendingRelationshipsTasks.each {
            it.run()
        }
    }

    static CatalogueElement preferDraft(CatalogueElement element) {
        if (element.status == ElementStatus.DRAFT) {
            return element
        }

        if (!element.latestVersionId) {
            return element
        }

        CatalogueElement existingDraft =  element.class.findByLatestVersionIdAndStatus(element.latestVersionId, ElementStatus.DRAFT, [sort: 'versionNumber', order: 'desc'])

        if (existingDraft) {
            return existingDraft
        }

        return element
    }
}
