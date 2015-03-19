package org.modelcatalogue.core.util.builder

import org.modelcatalogue.core.Classification

/**
 * Created by ladin on 18.03.15.
 */
class RelationshipDefinitionBuilder {

    final RelationshipDefinition definition

    RelationshipDefinitionBuilder(RelationshipDefinition definition) {
        this.definition = definition
    }

    RelationshipDefinitionBuilder withClassification(Classification classification) {
        definition.classification = classification
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

    RelationshipDefinitionBuilder withCombinedIndex(Long combinedIndex) {
        definition.combinedIndex = combinedIndex
        this
    }

    RelationshipDefinitionBuilder withParams(Map<String, Object> params) {
        params.each { String key, Object value ->
            if (key in ['source', 'destination', 'relationshipType']) {
                throw new IllegalArgumentException("Cannot set $key using the parameter map. Set the value directly when creating the builder")
            } else if (definition.hasProperty(key)) {
                definition.setProperty(key, value)
            } else {
                throw new IllegalArgumentException("Parameter $key not supported")
            }
        }
        this
    }
}
