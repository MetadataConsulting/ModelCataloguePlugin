package org.modelcatalogue.core

import groovy.transform.AutoClone

@AutoClone
class RelationshipDefinition {

    static RelationshipDefinitionBuilder create(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType) {
        return new RelationshipDefinitionBuilder(new RelationshipDefinition(source, destination, relationshipType))
    }

    static RelationshipDefinition from(Relationship relationship) {
        RelationshipDefinition definition = new RelationshipDefinition(relationship.source, relationship.destination, relationship.relationshipType)
        definition.with {
            dataModel = relationship.dataModel
            metadata = new LinkedHashMap<String, String>(relationship.ext)
            archived = relationship.archived
            outgoingIndex = relationship.outgoingIndex
            incomingIndex = relationship.incomingIndex
            combinedIndex = relationship.combinedIndex
        }
        definition
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
    DataModel dataModel = null
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
                dataModel: dataModel?.id ? dataModel : null,
                archived: archived,
                outgoingIndex: outgoingIndex ?: System.currentTimeMillis(),
                incomingIndex: incomingIndex ?: System.currentTimeMillis(),
                combinedIndex: combinedIndex ?: System.currentTimeMillis()
        )
    }

    @Override String toString() {
        "$source.name =[$relationshipType.sourceToDestination${dataModel ? ('/' + dataModel.name) : ''}]=> $destination.name"
    }
}

