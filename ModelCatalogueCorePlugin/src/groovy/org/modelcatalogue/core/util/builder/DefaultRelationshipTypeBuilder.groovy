package org.modelcatalogue.core.util.builder

import groovy.transform.CompileStatic
import org.modelcatalogue.builder.api.RelationshipTypeBuilder
import org.modelcatalogue.core.RelationshipType

@CompileStatic
class DefaultRelationshipTypeBuilder implements RelationshipTypeBuilder {

    final RelationshipType relationshipType

    DefaultRelationshipTypeBuilder(RelationshipType relationshipType) {
        this.relationshipType = relationshipType
    }

    @Override
    void sourceToDestination(String label) {
        relationshipType.sourceToDestination = label
    }

    @Override
    void sourceToDestination(Map<String, Object> parameters, String label) {
        relationshipType.sourceToDestination = label
        relationshipType.sourceToDestinationDescription = parameters.description
    }

    @Override
    void destinationToSource(String label) {
        relationshipType.destinationToSource = label
    }

    @Override
    void destinationToSource(Map<String, Object> parameters, String label) {
        relationshipType.destinationToSource = label
        relationshipType.destinationToSourceDescription = parameters.description
    }

    @Override
    void rule(String rule) {
        relationshipType.rule = rule
    }
}
