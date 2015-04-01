package org.modelcatalogue.core.util.builder

import groovy.transform.AutoClone
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType

@AutoClone
class RelationshipDefinition {

    static RelationshipDefinitionBuilder create(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType) {
        return new RelationshipDefinitionBuilder(new RelationshipDefinition(source, destination, relationshipType))
    }

    // required
    CatalogueElement source
    CatalogueElement destination
    RelationshipType relationshipType

    private RelationshipDefinition(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType) {
        if (!source) throw new IllegalArgumentException("Source cannot be null")
        if (!destination) throw new IllegalArgumentException("Destination cannot be null")
        if (!relationshipType) throw new IllegalArgumentException("Relationship type cannot be null")

        this.source = source
        this.destination = destination
        this.relationshipType = relationshipType
    }

    // optional
    Classification classification = null
    Map<String, String> metadata = [:]
    boolean archived
    Long outgoingIndex
    Long incomingIndex
    Long combinedIndex

    // processing flags
    boolean resetIndices
    boolean ignoreRules
    boolean skipUniqueChecking

    Relationship createRelationship() {
        new Relationship(
                source: source?.id ? source : null,
                destination: destination?.id ? destination : null,
                relationshipType: relationshipType?.id ? relationshipType : null,
                classification: classification?.id ? classification : null,
                archived: archived,
                outgoingIndex: outgoingIndex ?: System.currentTimeMillis(),
                incomingIndex: incomingIndex ?: System.currentTimeMillis(),
                combinedIndex: combinedIndex ?: System.currentTimeMillis()
        )
    }

    @Override String toString() {
        "$source.name =[$relationshipType.sourceToDestination${classification ? ('/' + classification.name) : ''}]=> $destination.name"
    }
}

