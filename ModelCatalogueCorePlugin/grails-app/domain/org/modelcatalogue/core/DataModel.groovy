package org.modelcatalogue.core

import org.modelcatalogue.core.publishing.PublishingChain
import org.modelcatalogue.core.publishing.PublishingContext
import org.modelcatalogue.core.util.Legacy

class DataModel extends CatalogueElement {

    String semanticVersion = '0.0.1'
    String revisionNotes

    /**
     * @deprecated use model catalogue id instead
     */
    String getNamespace() {
        modelCatalogueId
    }

    /**
     * @deprecated use model catalogue id instead
     */
    void setNamespace(String namespace) {
        modelCatalogueId = namespace
    }

    static constraints = {
        name unique: 'versionNumber'
        semanticVersion size: 1..20, nullable: true
        revisionNotes maxSize: 2000, nullable: true
    }

    static transients = ['namespace']

    static relationships = [
            outgoing: [classificationFilter: 'usedAsFilterBy', 'import': 'imports'],
            incoming: ['import': 'importedBy']
    ]

    @Override
    protected PublishingChain preparePublishChain(PublishingChain chain) {
        super.preparePublishChain(chain).add(this.declares)
    }


    @Override
    void setModelCatalogueId(String mcID) {
        super.setModelCatalogueId(Legacy.fixModelCatalogueId(mcID))
    }

    @Override
    String getDefaultModelCatalogueId(boolean withoutVersion = false) {
        // TODO: remove when the class is renamed
        return Legacy.fixModelCatalogueId(super.getDefaultModelCatalogueId(withoutVersion))
    }

    List<CatalogueElement> getDeclares() {
        CatalogueElement.findAllByDataModel(this)
    }

    Number countDeclares() {
        CatalogueElement.countByDataModel(this)
    }

    void checkNewSemanticVersion(String newSemanticVersion) {
        if (!newSemanticVersion) {
            errors.rejectValue('semanticVersion', 'dataModel.semanticVersion.null', 'Semantic version must be specified!')
            return
        }

        if (newSemanticVersion == semanticVersion) {
            rejectSemanticVersion()
        } else if (latestVersionId) {
            if (DataModel.countByLatestVersionIdAndSemanticVersion(latestVersionId, newSemanticVersion)) {
                rejectSemanticVersion()
            }
        }
    }

    void checkPublishSemanticVersion(String newSemanticVersion) {
        if (!newSemanticVersion) {
            errors.rejectValue('semanticVersion', 'dataModel.semanticVersion.null', 'Semantic version must be specified!')
            return
        }
        if (getLatestVersionId()) {
            if (DataModel.countByLatestVersionIdAndSemanticVersionAndIdNotEqual(getLatestVersionId(), newSemanticVersion, this.getId())) {
                rejectSemanticVersion()
            }
        }
    }

    /**
     * Checks whether {@link DataModel} can be finalized or not. When it is not eligible for finalization, it reject
     * values to errors of given class as a side effect.
     * Eligibility means:
     * <ul>
     *     <li>Semantic version needs to be unique and needs to be set.</li>
     *     <li>Revision notes needs to be filled.</li>
     *     <li>Metadata needs to be set (authors,reviewers, owner, reviewed, approved, namespace, organization).</li>
     * </ul>
     */
    void checkFinalizeEligibility() {
        // check semantic version
        checkPublishSemanticVersion(semanticVersion)

        // check revision notes
        if (!revisionNotes) {
            errors.rejectValue('revisionNotes', 'finalize.revisionNotes.null', 'Please, provide the revision notes')
        }

        // check basic metadata
        ["authors", "reviewers", "owner" , "reviewed" , "approved"].each {
            checkExtensionPresence(it)
        }

        // check namespace and organization
        ["namespace", "organization"].each {
            checkExtensionPresence(it)
        }
    }

    private void rejectSemanticVersion() {
        errors.rejectValue('semanticVersion', 'dataModel.semanticVersion.alreadyExist', 'Semantic version already exists for current data model!')
    }

    @Override
    void beforeDraftPersisted(PublishingContext context) {
        super.beforeDraftPersisted(context)
        revisionNotes = null
    }

    @Override
    String getDataModelSemanticVersion() {
        return semanticVersion
    }
}
