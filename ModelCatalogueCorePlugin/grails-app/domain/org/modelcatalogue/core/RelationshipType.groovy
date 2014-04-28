package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.SecuredRuleExecutor

class RelationshipType {

    //static elasticGormSearchable =

    static searchable = {
        name boost: 5
        sourceClass converter: RelationshipTypeClassConverter
        destinationClass converter: RelationshipTypeClassConverter
        except = ['rule','sourceClass','destinationClass', 'defaultRelationshipTypesDefinitions']
    }

    //name of the relationship type i.e. parentChild  or synonym
    String name

    // system relationship types are not returned from the controller
    Boolean system = false

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
        // cache usage: 'read-only'
    }

    String validateSourceDestination(CatalogueElement source, CatalogueElement destination) {

        if (!sourceClass.isInstance(source)) {
            return 'source.not.instance.of'
        }

        if (!destinationClass.isInstance(destination)) {
            return 'destination.not.instance.of'
        }

        if (rule && rule.trim() && !validateRule(source, destination)) {
            return 'rule.did.not.pass'
        }

        return null
    }

    boolean validateRule(CatalogueElement source, CatalogueElement destination) {
        if (!rule || !rule.trim()) {
            return true
        }

        new SecuredRuleExecutor(
                source: source,
                destination: destination,
                type: this
        ).execute(rule)
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

    Map<String, Object> getInfo() {
        [
                id: id,
                name: name,
                link: "/${GrailsNameUtils.getPropertyName(getClass())}/$id"
        ]
    }
}



