package org.modelcatalogue.core.publishing

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.api.ElementStatus

class DraftContext extends PublishingContext<DraftContext> {

    private boolean copyRelationships
    private boolean forceNew

    private Set<Long> elementsUnderControl
    private Set<Long> preferDraftsFor = new HashSet<Long>()

    private String semanticVersion

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

    DraftContext forceNew() {
        this.forceNew = true
        this
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

    DraftContext within(DataModel dataModel) {
        this.dataModel = dataModel
        return this
    }

    DraftContext version(String semanticVersion) {
        this.semanticVersion = semanticVersion
        return this
    }

    CatalogueElement findExisting(CatalogueElement element) {
        if (!element) {
            return element
        }

        //if it's already a draft return it
        if (element.status == ElementStatus.DRAFT || element.status == ElementStatus.UPDATED) {
            return element
        }

        //if there isn't a later version return it
        if (!element.latestVersionId) {
            return element
        }


        //if you should prefer the draft version
        if (element.id in preferDraftsFor || element.latestVersionId in preferDraftsFor || element.dataModel?.id in preferDraftsFor || element.dataModel?.latestVersionId in preferDraftsFor) {
            CatalogueElement existingDraft =  CatalogueElement.findByLatestVersionIdAndStatusInList(element.latestVersionId, [ElementStatus.DRAFT, ElementStatus.UPDATED], [sort: 'versionNumber', order: 'desc'])

            if (existingDraft) {
                return existingDraft
            }
        }


        //if you should prefer the finalised version (to the deprecated one)
        if(element.status != ElementStatus.FINALIZED ){

            CatalogueElement existingFinalised =  CatalogueElement.findByLatestVersionIdAndStatusInList(element.latestVersionId, [ElementStatus.FINALIZED], [sort: 'versionNumber', order: 'desc'])

            if (existingFinalised) {
                return existingFinalised
            }

        }

        return element
    }


    boolean hasVersion() {
        return semanticVersion != null
    }

    String getVersion() {
        return semanticVersion
    }

    @Override
    boolean shouldCopyRelationshipsFor(CatalogueElement draft) {
        return !(importFriendly && isUnderControl(draft))
    }

    @Override
    public String toString() {
        return "DraftContext{" +
            "copyRelationships=" + copyRelationships +
            ", forceNew=" + forceNew +
            ", dataModel='" + dataModel +  '\'' +
            ", semanticVersion='" + semanticVersion + '\'' +
            '}';
    }

    public DraftContext preferDraftFor(Long dataModelLatestVersionId) {
        preferDraftsFor << dataModelLatestVersionId
        return this
    }
}
