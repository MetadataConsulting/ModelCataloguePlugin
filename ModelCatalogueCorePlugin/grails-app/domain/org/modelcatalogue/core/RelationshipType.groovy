package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.SecuredRuleExecutor

class RelationshipType {

    def relationshipTypeService

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

    // comma separated list of metadata hints
    String metadataHints

    // if the direction of the relationship doesn't matter
    Boolean bidirectional = Boolean.FALSE

    /** if relationships of this type shall not be carried over when new draft version is created */
    Boolean versionSpecific = Boolean.FALSE

    /**
     * This is a script which will be evaluated with following binding:
     * source
     * destination
     * type
     *
     * Type stands for current type evaluated.
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
        metadataHints nullable: true, maxSize: 10000
        rule nullable: true, maxSize: 10000
    }


    static mapping = {
        // this makes entities immutable
        // cache usage: 'read-only'
        sort "name"
    }

    String validateSourceDestination(CatalogueElement source, CatalogueElement destination, Map<String, String> ext) {

        if (!sourceClass.isInstance(source)) {
            return 'source.not.instance.of'
        }

        if (!destinationClass.isInstance(destination)) {
            return 'destination.not.instance.of'
        }

        if (rule && rule.trim()) {
            def result = validateRule(source, destination, ext)
            if (result instanceof CharSequence) {
                return result
            }
            if (result instanceof Boolean && !result) {
                return 'rule.did.not.pass'
            }

            if ((result instanceof Boolean && result) || result == null) {
                return null
            }

            if (result instanceof Throwable) {
                log.info("Rule thrown an exception. This is slightly discouraged!", result)
                return result.message
            }

            if (result) {
                log.warn("Rule returned value which is not String or Boolean, this is very likely a bug. Result: $result")
            }
        }

        return null
    }

    def validateRule(CatalogueElement source, CatalogueElement destination, Map<String, String> ext) {
        if (!rule || !rule.trim()) {
            return true
        }

        new SecuredRuleExecutor(
                source: source,
                destination: destination,
                type: this,
                ext: ext
        ).execute(rule)
    }


    static getContainmentType() {
        readByName("containment")
    }

    static getClassificationType() {
        readByName("classification")
    }

    static getContextType() {
        readByName("context")
    }

    static getRelationshipType() {
        readByName("relationship")
    }

    static getHierarchyType() {
        readByName("hierarchy")
    }

    static getInclusionType() {
        readByName("inclusion")
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

    def beforeInsert() {
        relationshipTypeService.clearCache()
    }

    def beforeUpdate() {
        relationshipTypeService.clearCache()
    }

    def beforeDelete() {
        relationshipTypeService.clearCache()
    }


    static String toCamelCase(String text) {
        if (!text) return text
        def newParts = []
        text.split(/\s+/).eachWithIndex { it, index ->
            if (index > 0) {
                newParts << it.capitalize()
            } else {
                newParts << it
            }
        }
        newParts.join('')
    }
}



