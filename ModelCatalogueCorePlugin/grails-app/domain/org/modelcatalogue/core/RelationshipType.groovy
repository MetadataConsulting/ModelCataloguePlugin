package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class RelationshipType {

    static searchable = {
        name boost: 5
        except = ['rule', 'sourceClass', 'destinationClass', 'defaultRelationshipTypesDefinitions']
    }

    //name of the relationship type i.e. parentChild  or synonym
    String name

    //the both sides of the relationship ie. for parentChild this would be parent (for synonym this is synonym, so the same on both sides)
    String sourceToDestination

    //the both sides of the relationship i.e. for parentChild this would be child (for synonym this is synonym, so the same on both sides)
    String destinationToSource

    //you can constrain the relationship type
    Class sourceClass

    // you can constrain the relationship type
    Class destinationClass

    /**
     * This is a script which will be evaluated with following binding:
     * source
     * destination
     * type
     *
     * Type stands for current type evaluated
     *
     * For the beginning there are no constraints for the scripts so use them carefully.
     *
     */
    String rule

    static constraints = {
        def classValidator = { val, obj ->
            if (!val) return true
            if (!CatalogueElement.isAssignableFrom(val)) return "Only org.modelcatalogue.core.CatalogueElement child classes are allowed"
            return true
        }
        name unique: true, maxSize: 255, matches: /[a-z\-0-9A-Z]+/
        sourceToDestination maxSize: 255
        destinationToSource maxSize: 255
        sourceClass validator: classValidator
        destinationClass validator: classValidator
        rule nullable: true, maxSize: 1000
    }


    static mapping = {
        // this makes entities immutable
        cache usage: 'read-only'
    }

    boolean validateSourceDestination(CatalogueElement source, CatalogueElement destination) {

        if (!sourceClass.isInstance(source)) {
            return false
        }

        if (!destinationClass.isInstance(destination)) {
            return false
        }

        if (rule && rule.trim() && !validateRule(source, destination)) {
            return false
        }

        return true
    }

    boolean validateRule(CatalogueElement source, CatalogueElement destination) {
        if (!rule || !rule.trim()) {
            return true
        }

        GroovyShell shell = new GroovyShell(new Binding([
                source: source,
                destination: destination,
                type: this
        ]))
        shell.evaluate(rule)
    }


    static defaultRelationshipTypesDefinitions = [
            [name: "containment", sourceToDestination: "contains", destinationToSource: "contained in", sourceClass: Model, destinationClass: DataElement],
            [name: "context", sourceToDestination: "provides context for", destinationToSource: "has context of", sourceClass: ConceptualDomain, destinationClass: Model],
            [name: "hierarchy", sourceToDestination: "parent of", destinationToSource: "child of", sourceClass: Model, destinationClass: Model],
            [name: "inclusion", sourceToDestination: "includes", destinationToSource: "included in", sourceClass: ConceptualDomain, destinationClass: ValueDomain],
            [name: "instantiation", sourceToDestination: "instantiated by", destinationToSource: "instantiates", sourceClass: DataElement, destinationClass: ValueDomain],
            [name: "supersession", sourceToDestination: "superseded by", destinationToSource: "supersedes", sourceClass: PublishedElement, destinationClass: PublishedElement, rule: "source.class == destination.class"]

    ]

    static initDefaultRelationshipTypes() {
        for (definition in defaultRelationshipTypesDefinitions) {
            RelationshipType existing = findByName(definition.name)
            if (!existing) {
                new RelationshipType(definition).save()
            }
        }
    }

    static getContainmentType() {
        readByName("containment")
    }

    static getContextType() {
        readByName("context")
    }

    static getHierarchyType() {
        readByName("hierarchy")
    }

    static getInclusionType() {
        readByName("inclusion")
    }

    static getInstantiationType() {
        readByName("instantiation")
    }

    static getSupersessionType() {
        readByName("supersession")
    }

    static readByName(String name) {
        findByName(name, [readOnly: true])
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof RelationshipType)) {
            return false;
        }
        if (this.is(obj)) {
            return true;
        }
        RelationshipType de = (RelationshipType) obj;
        return new EqualsBuilder()
                .append(name, de.name)
                .isEquals()
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .toHashCode()
    }


    Map<String, Object> getInfo() {
        [
                id: id,
                name: name,
                link: "/${GrailsNameUtils.getPropertyName(getClass())}/$id"
        ]
    }
}



