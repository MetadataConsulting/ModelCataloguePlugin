package org.modelcatalogue.core

class RelationshipDefinitionBuilder {

    final RelationshipDefinition definition

    RelationshipDefinitionBuilder(RelationshipDefinition definition) {
        this.definition = definition
    }

    RelationshipDefinitionBuilder withDataModel(DataModel dataModel) {
        definition.dataModel = dataModel
        this
    }

    RelationshipDefinitionBuilder withMetadata(Map<String, String> metadata) {
        definition.metadata = metadata
        this
    }

    RelationshipDefinitionBuilder withArchived(boolean archived) {
        definition.archived = archived
        this
    }

    RelationshipDefinitionBuilder withInherited(boolean inherited) {
        definition.inherited = inherited
        this
    }

    RelationshipDefinitionBuilder withResetIndices(boolean resetIndices) {
        definition.resetIndices = resetIndices
        this
    }

    RelationshipDefinitionBuilder withIgnoreRules(boolean ignoreRules) {
        definition.ignoreRules = ignoreRules
        this
    }

    /**
     * If we expect the relationship not being present in the catalogue yet. This will cause throwing an exception
     * if the relationship already exists (or creating duplicate one if the constraint not present in database table).
     *
     * Use this if at least one end of this relationship is newly created instance so there is no chance to created
     * relationship exists (this for example applies on copying draft relationships or assigning classification
     * to newly created element).
     */
    RelationshipDefinitionBuilder withSkipUniqueChecking(boolean withSkipUniqueChecking) {
        definition.skipUniqueChecking = withSkipUniqueChecking
        this
    }

    RelationshipDefinitionBuilder withOutgoingIndex(Long outgoingIndex) {
        definition.outgoingIndex = outgoingIndex
        this
    }

    RelationshipDefinitionBuilder withIncomingIndex(Long incomingIndex) {
        definition.incomingIndex = incomingIndex
        this
    }


    RelationshipDefinitionBuilder withParams(Map<String, Object> params) {
        params.each { String key, Object value ->
            if (key in ['source', 'destination', 'relationshipType']) {
                throw new IllegalArgumentException("Cannot set $key using the parameter map. Set the value directly when creating the builder")
            } else if (definition.hasProperty(key)) {
                definition.setProperty(key, value)
            } else {
                if (key == 'classification') {
                    definition.dataModel = value as DataModel
                    return
                }
                throw new IllegalArgumentException("Parameter $key not supported")
            }
        }
        this
    }
}
