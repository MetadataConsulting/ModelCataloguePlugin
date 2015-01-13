package org.modelcatalogue.core.publishing

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ElementStatus

/**
 * Created by ladin on 09.01.15.
 */
class DraftContext {

    private boolean copyRelationships

    private Set<CopyAssociationsAndRelationships> pendingRelationshipsTasks = new LinkedHashSet<CopyAssociationsAndRelationships>()

    private DraftContext(boolean copyRelationships) {
        this.copyRelationships = copyRelationships
    }
    static DraftContext userFriendly() {
        new DraftContext(true)
    }

    static DraftContext importFriendly() {
        new DraftContext(false)
    }

    void delayRelationshipCopying(CatalogueElement draft, CatalogueElement oldVersion) {
        pendingRelationshipsTasks << new CopyAssociationsAndRelationships(draft, oldVersion, !copyRelationships)
    }

    void classifyDrafts() {
        pendingRelationshipsTasks.each {
            it.copyClassifications()
        }
    }

    void resolvePendingRelationships() {
        pendingRelationshipsTasks.each {
            it.copyRelationships()
        }
    }

    static CatalogueElement preferDraft(CatalogueElement element) {
        if (element.status == ElementStatus.DRAFT || element.status == ElementStatus.UPDATED) {
            return element
        }

        if (!element.latestVersionId) {
            return element
        }

        CatalogueElement existingDraft =  element.class.findByLatestVersionIdAndStatusInList(element.latestVersionId, [ElementStatus.DRAFT, ElementStatus.UPDATED], [sort: 'versionNumber', order: 'desc'])

        if (existingDraft) {
            return existingDraft
        }

        return element
    }
}
