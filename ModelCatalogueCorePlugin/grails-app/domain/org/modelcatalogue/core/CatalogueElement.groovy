package org.modelcatalogue.core

import grails.util.GrailsNameUtils

/**
* Catalogue Element - there are a number of catalogue elements that make up the model
* catalogue (please see DataType, ConceptualDomain, MeasurementUnit, Model, ValueDomain,
* DataElement) they extend catalogue element which allows creation of incoming and outgoing
* relationships between them. They also  share a number of characteristics.
* */

abstract class CatalogueElement {

    def relationshipService

    String name
    String description

    static transients = ['relations', 'info', 'archived']

    static hasMany = [incomingRelationships: Relationship, outgoingRelationships: Relationship, outgoingMappings: Mapping,  incomingMappings: Mapping]

    static constraints = {
        name size: 1..255
        description nullable: true, maxSize: 2000
    }

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
        name boost:5
        incomingMappings component: true
        except = ['incomingRelationships', 'outgoingRelationships', 'incomingMappings', 'outgoingMappings']
    }

    static mapping = {
        sort "name"
        description type: "text"
    }

    static mappedBy = [outgoingRelationships: 'source', incomingRelationships: 'destination', outgoingMappings: 'source', incomingMappings: 'destination']

    /**
     * Functions for specifying relationships between catalogue elements using the
     * org.modelcatalogue.core.Relationship class
     * @return list of items which contains incoming and outgoing relations
     */

    List getRelations() {
        return [
                (outgoingRelationships ?: []).collect { it.destination },
                (incomingRelationships ?: []).collect { it.source }
        ].flatten()
    }

    List getIncomingRelations() {
        return [
                (incomingRelationships ?: []).collect { it.source }
        ].flatten()
    }

    List getOutgoingRelations() {
        return [
                (outgoingRelationships ?: []).collect { it.destination }
        ].flatten()
    }


    List getIncomingRelationsByType(RelationshipType type) {
        if (archived) {
            return Relationship.findAllByDestinationAndRelationshipType(this, type).collect {
                it.source
            }
        }
        Relationship.findAllByDestinationAndRelationshipTypeAndArchived(this, type, false).collect {
            it.source
        }
    }

    List getOutgoingRelationsByType(RelationshipType type) {
        if (archived) {
            return Relationship.findAllBySourceAndRelationshipType(this, type).collect {
                it.destination
            }
        }
        Relationship.findAllBySourceAndRelationshipTypeAndArchived(this, type, false).collect {
            it.destination
        }
    }

    List getRelationsByType(RelationshipType type) {
        [getOutgoingRelationsByType(type), getIncomingRelationsByType(type)].flatten()
    }

    int countIncomingRelationsByType(RelationshipType type) {
        CatalogueElement self = this.isAttached() ? this : get(this.id)
        if (archived) {
            return Relationship.countByDestinationAndRelationshipType(self, type)
        }
        Relationship.countByDestinationAndRelationshipTypeAndArchived(self, type, false)

    }

    int countOutgoingRelationsByType(RelationshipType type) {
        CatalogueElement self = this.isAttached() ? this : get(this.id)
        if (archived) {
            return Relationship.countBySourceAndRelationshipType(self, type)
        }
        Relationship.countBySourceAndRelationshipTypeAndArchived(self, type, false)
    }

    int countRelationsByType(RelationshipType type) {
        countOutgoingRelationsByType(type) + countIncomingRelationsByType(type)
    }


    Relationship createLinkTo(CatalogueElement destination, RelationshipType type) {
        relationshipService.link(this, destination, type)
    }

    Relationship createLinkFrom(CatalogueElement source, RelationshipType type) {
        relationshipService.link(source, this, type)
    }

    Relationship removeLinkTo(CatalogueElement destination, RelationshipType type) {
        relationshipService.unlink(this, destination, type)
    }

    Relationship removeLinkFrom(CatalogueElement source, RelationshipType type) {
        relationshipService.unlink(source, this, type)
    }

    String toString() {
        "${getClass().simpleName}[id: ${getId()}, name: ${getName()}]"
    }

    Map<String, Object> getInfo() {
        [
                id: getId(),
                name: name,
                link: "/${GrailsNameUtils.getPropertyName(getClass())}/${getId()}"
        ]
    }

    boolean isArchived() { false }

    def beforeDelete(){
        outgoingRelationships.each{ relationship->
            relationship.beforeDelete()
            relationship.delete(flush:true)
        }
        incomingRelationships.each{ relationship ->
            relationship.beforeDelete()
            relationship.delete(flush:true)
        }
        outgoingMappings.each{ mapping ->
            mapping.beforeDelete()
            mapping.delete(flush:true)
        }
        incomingMappings.each{ mapping ->
            mapping.beforeDelete()
            mapping.delete(flush:true)
        }
    }
}