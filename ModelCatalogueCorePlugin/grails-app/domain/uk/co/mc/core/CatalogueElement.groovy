package uk.co.mc.core

import grails.util.GrailsNameUtils
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/*
* Catalogue Element - there are a number of catalogue elements that make up the model catalogue (please see
* DataType, ConceptualDomain, MeasurementUnit, Model, ValueDomain, DataElement)
* they extend catalogue element which allows creation of incoming and outgoing
* relationships between them. They also  share a number of characteristics.
* */

abstract class CatalogueElement {

    String name
    String description

    static hasMany = [incomingRelationships: Relationship, outgoingRelationships: Relationship]

    static constraints = {
        name size: 1..255
        description nullable: true, maxSize: 2000
    }

    static mapping = {
        description type: "text"
    }


    static mappedBy = [outgoingRelationships: 'source', incomingRelationships: 'destination']

    static transients = ['relations', 'info']

    /** ****************************************************************************************************************/
    /****functions for specifying relationships between catalogue elements using the uk.co.mc.core.Relationship class ************/
    /** ****************************************************************************************************************/

    /***********return all the relations************/

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


    Relationship createLinkTo(CatalogueElement destination, RelationshipType type) {
        Relationship.link(this, destination, type)
    }

    Relationship createLinkFrom(CatalogueElement source, RelationshipType type) {
        Relationship.link(source, this, type)
    }

    void removeLinkTo(CatalogueElement destination, RelationshipType type) {
        Relationship.unlink(this, destination, type)
    }

    void removeLinkFrom(CatalogueElement source, RelationshipType type) {
        Relationship.unlink(source, this, type)
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

    public boolean equals(Object obj) {
        if (!(obj instanceof CatalogueElement)) {
            return false;
        }
        if (this.is(obj)) {
            return true;
        }
        CatalogueElement ce = (CatalogueElement) obj;
        return new EqualsBuilder()
                .append(name, ce.name)
                .append(id, ce.id)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(id)
                .toHashCode();
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
    }
}