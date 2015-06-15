package org.modelcatalogue.core.publishing

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ElementStatus
import org.modelcatalogue.core.RelationshipType

class DraftContext {

    private boolean copyRelationships
    private boolean forceNew

    private Set<Long> elementsUnderControl

    private Set<CopyAssociationsAndRelationships> pendingRelationshipsTasks = new LinkedHashSet<CopyAssociationsAndRelationships>()
    private Set<String> createdRelationshipHashes = []

    private DraftContext(boolean copyRelationships, Set<Long> elementsUnderControl) {
        this.copyRelationships = copyRelationships
        this.elementsUnderControl = Collections.unmodifiableSet(elementsUnderControl)
    }
    static DraftContext userFriendly() {
        new DraftContext(true, [] as Set)
    }

    static DraftContext importFriendly(Set<Long> elementsUnderControl) {
        new DraftContext(false, elementsUnderControl)
    }

    static DraftContext forceNew() {
        DraftContext context = new DraftContext(true, [] as Set)
        context.forceNew = true
        context
    }

    boolean isForceNew() {
        return forceNew
    }

    boolean isImportFriendly() {
        return !copyRelationships
    }

    boolean isUnderControl(CatalogueElement element) {
        if (element.getLatestVersionId()) {
            return element.getLatestVersionId() in elementsUnderControl
        }
        return element.getId() in elementsUnderControl
    }

    void stopForcingNew() {
        forceNew = false
    }

    void delayRelationshipCopying(CatalogueElement draft, CatalogueElement oldVersion) {
        pendingRelationshipsTasks << new CopyAssociationsAndRelationships(draft, oldVersion, this)
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
