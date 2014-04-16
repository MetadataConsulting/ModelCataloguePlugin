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

    static transients = ['relations', 'info']

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
        Relationship.findAllByDestinationAndRelationshipType(this, type).collect {
            it.source
        }
    }

    List getOutgoingRelationsByType(RelationshipType type) {
        Relationship.findAllBySourceAndRelationshipType(this, type).collect {
            it.destination
        }
    }

    List getRelationsByType(RelationshipType type) {
        [getOutgoingRelationsByType(type), getIncomingRelationsByType(type)].flatten()
    }

    int countIncomingRelationsByType(RelationshipType type) {
        if (this.isAttached()) {
            Relationship.countByDestinationAndRelationshipType(this, type)
        }else{
            Relationship.countByDestinationAndRelationshipType(get(this.id), type)
        }

    }

    int countOutgoingRelationsByType(RelationshipType type) {
        if (this.isAttached()) {
            Relationship.countBySourceAndRelationshipType(this, type)
        }else{
            Relationship.countBySourceAndRelationshipType(get(this.id), type)
        }
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
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

    Map<String, Object> getInfo() {
        [
                id: id,
                name: name,
                link: "/${GrailsNameUtils.getPropertyName(getClass())}/$id"
        ]
    }

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