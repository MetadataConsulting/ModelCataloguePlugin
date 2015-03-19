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

    RelationshipDefinitionBuilder withNewExpected(boolean newExpected) {
        definition.newExpected = newExpected
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
