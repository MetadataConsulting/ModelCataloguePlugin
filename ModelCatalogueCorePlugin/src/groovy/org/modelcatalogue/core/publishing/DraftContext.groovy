package org.modelcatalogue.core.publishing

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ElementStatus
import org.modelcatalogue.core.RelationshipType

/**
 * Created by ladin on 09.01.15.
 */
class DraftContext {

    private boolean copyRelationships
    private boolean forceNew

    private Set<CopyAssociationsAndRelationships> pendingRelationshipsTasks = new LinkedHashSet<CopyAssociationsAndRelationships>()
    private Set<String> createdRelationshipHashes = []

    private DraftContext(boolean copyRelationships) {
        this.copyRelationships = copyRelationships
    }
    static DraftContext userFriendly() {
        new DraftContext(true)
    }

    static DraftContext importFriendly() {
        new DraftContext(false)
    }

    static DraftContext forceNew() {
        DraftContext context = new DraftContext(true)
        context.forceNew = true
        context
    }

    boolean isForceNew() {
        return forceNew
    }

    void stopForcingNew() {
        forceNew = false
    }

    void delayRelationshipCopying(CatalogueElement draft, CatalogueElement oldVersion) {
        pendingRelationshipsTasks << new CopyAssociationsAndRelationships(draft, oldVersion, !copyRelationships)
    }

    void classifyDrafts() {
        pendingRelationshipsTasks.each {
            it.copyClassifications(createdRelationshipHashes)
        }
    }

    void resolvePendingRelationships() {
        pendingRelationshipsTasks.each {
            it.copyRelationships(createdRelationshipHashes)
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

    static String hashForRelationship(CatalogueElement source, CatalogueElement destination, RelationshipType type) {
        "$source.id:$type.id:$destination.id"
    }
}
