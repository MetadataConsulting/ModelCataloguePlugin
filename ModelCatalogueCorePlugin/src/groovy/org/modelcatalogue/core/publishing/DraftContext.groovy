package org.modelcatalogue.core.publishing

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.RelationshipType

class DraftContext {

    private boolean copyRelationships
    private boolean forceNew

    private Set<Long> elementsUnderControl

    private DataModel dataModel
    private String semanticVersion

    private Class<? extends CatalogueElement> newType

    private Set<CopyAssociationsAndRelationships> pendingRelationshipsTasks = new LinkedHashSet<CopyAssociationsAndRelationships>()
    private Set<String> createdRelationshipHashes = []

    private DraftContext(boolean copyRelationships, Set<Long> elementsUnderControl, Class newType) {
        this.copyRelationships = copyRelationships
        this.elementsUnderControl = Collections.unmodifiableSet(elementsUnderControl)
        this.newType = newType
    }

    static DraftContext typeChangingUserFriendly(Class<? extends CatalogueElement> newType) {
        DraftContext context = new DraftContext(true, [] as Set, newType)
        context.forceNew = true
        context
    }

    static DraftContext typeChangingImportFriendly(Class<? extends CatalogueElement> newType, Set<Long> elementsUnderControl) {
        DraftContext context = new DraftContext(false, elementsUnderControl, newType)
        context.forceNew = true
        context
    }

    static DraftContext userFriendly() {
        new DraftContext(true, [] as Set, null)
    }

    static DraftContext importFriendly(Set<Long> elementsUnderControl) {
        new DraftContext(false, elementsUnderControl, null)
    }

    static DraftContext forceNew() {
        DraftContext context = new DraftContext(true, [] as Set, null)
        context.forceNew = true
        context
    }

    Class<? extends CatalogueElement> getNewType() {
        newType
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

    void resolvePendingRelationships() {
        pendingRelationshipsTasks.each {
            it.copyRelationships(dataModel, createdRelationshipHashes)
        }
    }

    DataModel getDataModel() {
        return dataModel
    }

    DraftContext within(DataModel dataModel) {
        this.dataModel = dataModel
        return this
    }

    DraftContext version(String semanticVersion) {
        this.semanticVersion = semanticVersion
        return this
    }

    static <E extends CatalogueElement> E preferDraft(E element) {
        if (!element) {
            return element
        }
        if (element.status == ElementStatus.DRAFT || element.status == ElementStatus.UPDATED) {
            return element
        }

        if (!element.latestVersionId) {
            return element
        }

        E existingDraft =  element.class.findByLatestVersionIdAndStatusInList(element.latestVersionId, [ElementStatus.DRAFT, ElementStatus.UPDATED], [sort: 'versionNumber', order: 'desc'])

        if (existingDraft) {
            return existingDraft
        }

        return element
    }

    static String hashForRelationship(CatalogueElement source, CatalogueElement destination, RelationshipType type) {
        "$source.id:$type.id:$destination.id"
    }

    static String nextPatchVersion(Object patchVersion) {
        String currentVersion = patchVersion?.toString()
        if (!currentVersion) {
            // null is considered to be 0.0.1
            return '0.0.2'
        }

        def majorMinorPatch = currentVersion =~ /^(\d+)(\.(\d+))?(\.(\d+))?$/

        if (majorMinorPatch) {
            Integer major = majorMinorPatch[0][1] as Integer
            Integer minor = majorMinorPatch[0][3] as Integer ?: 0
            Integer patch = majorMinorPatch[0][5] as Integer ?: 0

            return "${major}.${minor}.${patch + 1}"
        }

        def numberSuffix = currentVersion =~ /(.*?)(\d+)$/

        if (numberSuffix) {
            String base = numberSuffix[0][1]
            String suffix = numberSuffix[0][2]

            Integer suffixAsNumber = suffix as Integer

            return base + ("${suffixAsNumber + 1}".padLeft(suffix.size(), '0'))
        }

        return "${currentVersion}-01"
    }

    DataModel getDestinationDataModel(CatalogueElement catalogueElement) {
        if (!catalogueElement) {
            return null
        }

        if (catalogueElement.instanceOf(DataModel)) {
            return null
        }
        if (catalogueElement.dataModel) {
            return preferDraft(catalogueElement.dataModel)
        }
        if (dataModel) {
           return preferDraft(dataModel)
        }
        return null
    }

    boolean hasVersion() {
        return semanticVersion != null
    }

    String getVersion() {
        return semanticVersion
    }


    @Override
    public String toString() {
        return "DraftContext{" +
            "copyRelationships=" + copyRelationships +
            ", forceNew=" + forceNew +
            ", dataModel=" + dataModel +
            ", semanticVersion='" + semanticVersion + '\'' +
            ", newType=" + newType +
            '}';
    }
}
