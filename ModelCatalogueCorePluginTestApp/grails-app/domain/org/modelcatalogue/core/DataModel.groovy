package org.modelcatalogue.core

import static org.modelcatalogue.core.policy.VerificationPhase.FINALIZATION_CHECK
import com.google.common.collect.ImmutableList
import com.google.common.collect.Iterables
import groovy.transform.EqualsAndHashCode
import org.modelcatalogue.core.policy.Convention
import org.modelcatalogue.core.publishing.PublishingChain
import org.modelcatalogue.core.publishing.PublishingContext
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.Legacy

class DataModel extends CatalogueElement {

    String semanticVersion = '0.0.1'
    String revisionNotes

    static constraints = {
        name unique: 'semanticVersion'
        semanticVersion size: 1..20, nullable: true
        revisionNotes maxSize: 2000, nullable: true
    }

    static transients = ['namespace', 'dataModelPolicies']

    static relationships = [
        outgoing: [classificationFilter: 'usedAsFilterBy', 'import': 'imports'],
        incoming: ['import': 'importedBy']
    ]

    static hasMany = [policies: DataModelPolicy, outgoingRelationships: Relationship, outgoingMappings: Mapping,  incomingMappings: Mapping, extensions: ExtensionValue]

    static mapping = {
        sort 'name'
        policies lazy: false
    }

    @Override
    protected PublishingChain preparePublishChain(PublishingChain chain) {
        super.preparePublishChain(chain).add(this.declares)
    }

    @Override
    Map<CatalogueElement, Object> manualDeleteRelationships(DataModel toBeDeleted) {
        // inspect declarations
        def manualDelete = declares.collectEntries {
            // check the element for the same - if manual delete is needed
            def relationshipManualDelete = it.manualDeleteRelationships(this)
            if (relationshipManualDelete.size() > 0)
                return [(it): relationshipManualDelete]
            else
                return [:]
        }
        // inspect relationships
        manualDelete << (outgoingRelationships + incomingRelationships).collectEntries {
            if (it.dataModel && it.dataModel != toBeDeleted)
                return [(it.dataModel): it]
            else
                return [:]
        }

        return manualDelete
    }

    /**
     * Deletes all {@link CatalogueElement}s assigned to this {@link DataModel} and then call super class method
     * {@link CatalogueElement#deleteRelationships()}.
     */
    @Override
    void deleteRelationships() {
        // delete all declarations, in following order: DataElements, DataTypes, DataClasses and anything else
        declares.sort { a, b ->
            def sortMap = [(DataElement): 0, (DataType): 1, (DataClass): 2]
            def aSort = sortMap.get(a.class) != null ? sortMap.get(a.class) : 3
            def bSort = sortMap.get(b.class) != null ? sortMap.get(b.class) : 3
            aSort <=> bSort
        }.each {
            it.deleteRelationships()
            it.delete()
        }
        super.deleteRelationships()
    }

    @Override
    void setModelCatalogueId(String mcID) {
        super.setModelCatalogueId(Legacy.fixModelCatalogueId(mcID))
    }

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

    List<CatalogueElement> getDeclares() {
        if (!readyForQueries) {
            return Collections.emptyList()
        }
        CatalogueElement.findAllByDataModel(this)
    }

    Number countDeclares() {
        if (!readyForQueries) {
            return 0
        }
        CatalogueElement.countByDataModel(this)
    }

    List<DataElement> getDataElements() {
        if (!readyForQueries) {
            return Collections.emptyList()
        }
        DataElement.findAllByDataModel(this)
    }

    List<DataType> getDataTypes() {
        if (!readyForQueries) {
            return Collections.emptyList()
        }
        DataType.findAllByDataModel(this)
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
     * @param semanticVersion Version to be used for finalized data model.
     * @param revisionNotes Revision notes to be used for finalized data model.
     */
    void checkFinalizeEligibility(String semanticVersion, String revisionNotes) {
        // initialize error object
        validate()

        // check semantic version
        checkPublishSemanticVersion(semanticVersion)

        // check revision notes
        if (!revisionNotes)
            errors.rejectValue('revisionNotes', 'finalize.revisionNotes.null', 'Please, provide the revision notes')

        if (policies) {
            for (CatalogueElement element in Iterables.concat(ImmutableList.of(this), this.declares)) {
                for (DataModelPolicy policy in policies) {
                    for (Convention convention in policy.policy.conventions) {
                        if (element.instanceOf(convention.target)) {
                            convention.verify(FINALIZATION_CHECK, this, element, false)
                        }
                    }
                }
            }
        }

//        // check revision notes
//        if (!revisionNotes)
//            errors.rejectValue('revisionNotes', 'finalize.revisionNotes.null', 'Please, provide the revision notes')
//
//        // check basic metadata
//        ["authors", "reviewers", "owner", "reviewed", "approved"].each {
//            checkExtensionPresence(it)
//        }
//
//        // check namespace and organization
//        ["namespace", "organization"].each {
//            checkExtensionPresence(it)
//        }
//
//        // check all data element have type
//        def wrongDataElements = getDataElements().findAll { !it.dataType }
//        if (wrongDataElements.size() > 0) {
//            errors.reject("dataModel.dataElements.dataType.null",
//                "All data elements must have data types! (See ${wrongDataElements.collect { it.name }}.)")
//        }
//
//        // check all data types doesn't contains dash, underscore or space
//        def wrongDataTypes = getDataTypes().findAll { !(it.name ==~ /[^_ -]+/) }
//        if (wrongDataTypes.size() > 0) {
//            errors.reject("dataModel.dataTypes.camelCase",
//                "All data types names must not contain space, dash and underscore characters: '-', '_', ' ')! (See ${wrongDataTypes.collect { it.name }}.)")
//        }
//
//        // check data elements have unique names
//        def dataElements = getDataElements()
//        def dataElementsUnique = dataElements.unique(false) { a, b -> a.name <=> b.name }
//        if (dataElements.size() != dataElementsUnique.size()) {
//            errors.reject("dataModel.dataElements.unique",
//                "All data elements names must be unique, there are duplicate entries. (See " +
//                    "${(dataElements - dataElementsUnique).collect { it.name }.unique()}.)")
//        }
//
//        // check data types have unique names
//        def dataTypes = getDataTypes()
//        def dataTypesUnique = dataTypes.unique(false) { a, b -> a.name <=> b.name }
//        if (dataTypes.size() != dataTypesUnique.size()) {
//            errors.reject("dataModel.dataTypes.unique",
//                "All data type names must be unique, there are duplicate entries. (See " +
//                    "${(dataTypes - dataTypesUnique).collect { it.name }.unique()}.)")
//        }
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
    void afterDraftPersisted(CatalogueElement draft, PublishingContext context) {
        super.afterDraftPersisted(draft, context)
        if (policies) {
            for(DataModelPolicy policy in policies) {
                draft.addToPolicies(policy)
            }
            FriendlyErrors.failFriendlySave(draft)
        }
    }


}
