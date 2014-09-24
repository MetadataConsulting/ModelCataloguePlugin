package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.RelationshipDirection

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
	String modelCatalogueId = "MC_" + UUID.randomUUID() + "_" + 1


    // time stamping
    Date dateCreated
    Date lastUpdated

    //stop null pointers (especially deleting new items)
    Set<Relationship> incomingRelationships = []
    Set<Relationship> outgoingRelationships = []
    Set<Mapping> outgoingMappings = []
    Set<Mapping> incomingMappings = []

    static transients = ['relations', 'info', 'archived', 'incomingRelations', 'outgoingRelations', 'classifiedName']

    static hasMany = [incomingRelationships: Relationship, outgoingRelationships: Relationship, outgoingMappings: Mapping,  incomingMappings: Mapping]

    static relationships = [
            incoming: [base: 'isBasedOn'],
            outgoing: [base: 'isBaseFor', attachment: 'hasAttachmentOf'],
            bidirectional: [relatedTo: 'relatedTo']
    ]

    static constraints = {
        name size: 1..255
        description nullable: true, maxSize: 2000
		modelCatalogueId bindable: false, nullable: true, unique: true, maxSize: 255, matches: '(?i)MC_([A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12})_\\d+'
        dateCreated bindable: false
        lastUpdated bindable: false
        archived bindable: false
    }

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
		modelCatalogueId boost:10
        name boost:5
        incomingMappings component: true
        except = ['incomingRelationships', 'outgoingRelationships', 'incomingMappings', 'outgoingMappings']
    }

    static mapping = {
        tablePerHierarchy false
        sort "name"
        description type: "text"
    }

    static mappedBy = [outgoingRelationships: 'source', incomingRelationships: 'destination', outgoingMappings: 'source', incomingMappings: 'destination']

    /**
     * Functions for specifying relationships between catalogue elements using the
     * org.modelcatalogue.core.Relationship class
     * @return list of items which contains incoming and outgoing relations
     */

    String getClassifiedName() { name }

    List getRelations() {
        return [
                outgoingRelations,
                incomingRelations
        ].flatten()
    }

    List getIncomingRelations() {
        relationshipService.getRelationships([:], RelationshipDirection.INCOMING, this).items.collect { it.source }
    }

    List getOutgoingRelations() {
        relationshipService.getRelationships([:], RelationshipDirection.OUTGOING, this).items.collect { it.destination }
    }

    Long countIncomingRelations() {
        CatalogueElement refreshed = getClass().get(this.id)
        if (!refreshed) return 0
        relationshipService.getRelationships([:], RelationshipDirection.INCOMING, refreshed).total
    }

    Long countOutgoingRelations() {
        CatalogueElement refreshed = getClass().get(this.id)
        if (!refreshed) return 0
        relationshipService.getRelationships([:], RelationshipDirection.OUTGOING, refreshed).total
    }

    Long countRelations() {
        CatalogueElement refreshed = getClass().get(this.id)
        if (!refreshed) return 0
        relationshipService.getRelationships([:], RelationshipDirection.BOTH, refreshed).total
    }


    List getIncomingRelationsByType(RelationshipType type) {
        ListWithTotal<Relationship> relationships = relationshipService.getRelationships([:], RelationshipDirection.INCOMING, this, type)
        relationships.items.collect {
            it.source
        }
    }

    List getOutgoingRelationsByType(RelationshipType type) {
        ListWithTotal<Relationship> relationships = relationshipService.getRelationships([:], RelationshipDirection.OUTGOING, this, type)
        relationships.items.collect {
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
        "${getClass().simpleName}[id: ${getId()}, name: ${getName()}, modelCatalogueId: ${modelCatalogueId}]"
    }

    Map<String, Object> getInfo() {
        [
                id: getId(),
                name: name,
                link: "/${GrailsNameUtils.getPropertyName(getClass())}/${getId()}"
        ]
    }

    boolean isArchived() { false }



    def afterInsert(){
       if(!getModelCatalogueId()) {
           createModelCatalogueId()
       }
    }

    def createModelCatalogueId(){
        modelCatalogueId = "MC_" + UUID.randomUUID() + "_" + 1
    }

    def beforeDelete(){
        new HashSet(outgoingRelationships).each{ Relationship relationship->
            relationship.beforeDelete()
            relationship.delete(flush:true)
        }
        new HashSet(incomingRelationships).each{ Relationship relationship ->
            relationship.beforeDelete()
            relationship.delete(flush:true)
        }
        new HashSet(outgoingMappings).each{ Mapping mapping ->
            mapping.beforeDelete()
            mapping.delete(flush:true)
        }
        new HashSet(incomingMappings).each{ Mapping mapping ->
            mapping.beforeDelete()
            mapping.delete(flush:true)
        }
    }

	def updateModelCatalogueId() {
		def newCatalogueId = modelCatalogueId.split("_")
		newCatalogueId[-1] = newCatalogueId.last().toInteger() + 1
		modelCatalogueId = newCatalogueId.join("_")
	}



	/**
	 * Get the Model Catalogue ID excluding any version information suffix.
	 * @return The model catalogue ID, minus any trailing underscore and version numbers
	 */
	def getBareModelCatalogueId() {
		// Match everything from the ID except the final underscore and integers ('_\d+')
		(modelCatalogueId =~ /(?i)(MC_([A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12}))/)[0][1]
	}
}