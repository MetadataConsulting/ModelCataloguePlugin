package org.modelcatalogue.core

import org.modelcatalogue.core.publishing.PublishingChain
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

    @Override
    protected PublishingChain prepareDraftChain(PublishingChain chain) {
        return super.prepareDraftChain(chain).add(this.declares)
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

    private void rejectSemanticVersion() {
        errors.rejectValue('semanticVersion', 'dataModel.semanticVersion.alreadyExist', 'Semantic version already exists for current data model!')
    }

    @Override
    void beforeDraftPersisted() {
        super.beforeDraftPersisted()
        revisionNotes = null
    }

    @Override
    String getDataModelSemanticVersion() {
        return semanticVersion
    }

    String toString() {
        "${getClass().simpleName}[semanticVersion: ${semanticVersion}, id: ${id}, name: ${name}, status: ${status}, modelCatalogueId: ${modelCatalogueId}]"
    }
}
