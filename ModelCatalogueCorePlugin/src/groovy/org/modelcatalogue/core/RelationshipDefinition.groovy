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
    boolean inherited
    Long outgoingIndex
    Long incomingIndex

    // processing flags
    boolean resetIndices
    boolean ignoreRules
    boolean skipUniqueChecking
    boolean otherSide

    Relationship createRelationship() {
        new Relationship(
                source: source?.id ? source : null,
                destination: destination?.id ? destination : null,
                relationshipType: relationshipType?.id ? relationshipType : null,
                dataModel: dataModel?.id ? dataModel : null,
                archived: archived,
                inherited: inherited,
                outgoingIndex: outgoingIndex ?: System.currentTimeMillis(),
                incomingIndex: incomingIndex ?: System.currentTimeMillis()
        )
    }

    RelationshipDefinition inverted() {
        RelationshipDefinition definition = new RelationshipDefinition(destination, source, relationshipType)

        definition.dataModel = this.dataModel
        definition.metadata = this.metadata
        definition.archived = this.archived
        definition.inherited = this.inherited
        definition.outgoingIndex = this.outgoingIndex
        definition.incomingIndex = this.incomingIndex
        definition.resetIndices = this.resetIndices
        definition.ignoreRules = this.ignoreRules
        definition.skipUniqueChecking = this.skipUniqueChecking
        definition.otherSide = !this.otherSide

        return definition

    }


    @Override String toString() {
        "$source.name =[$relationshipType.sourceToDestination${dataModel ? ('/' + dataModel.name) : ''}]=> $destination.name"
    }
}

