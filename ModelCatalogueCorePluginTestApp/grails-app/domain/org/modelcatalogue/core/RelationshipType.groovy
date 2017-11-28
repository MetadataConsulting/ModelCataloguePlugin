package org.modelcatalogue.core

import com.google.common.collect.ImmutableMap
import com.google.common.util.concurrent.UncheckedExecutionException
import grails.util.GrailsNameUtils
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.exceptions.DefaultStackTraceFilterer
import org.codehaus.groovy.grails.exceptions.StackTraceFilterer
import org.modelcatalogue.core.cache.CacheService
import org.modelcatalogue.core.rx.ErrorSubscriber
import org.modelcatalogue.core.util.RelationshipTypeRuleScript
import org.modelcatalogue.core.util.SecuredRuleExecutor
import java.util.concurrent.Callable

class RelationshipType implements org.modelcatalogue.core.api.RelationshipType {

    def relationshipTypeService
    def modelCatalogueSearchService

    //name of the relationship type i.e. parentChild  or synonym
    String name

    // system relationship types are not returned from the controller
    Boolean system = false

    // searchable relationships are index and reindex each time the source or destination changes
    Boolean searchable = false

    //the both sides of the relationship ie. for parentChild this would be parent (for synonym this is synonym, so the same on both sides)
    String sourceToDestination

    // detailed explanation of the source to destination relationship
    String sourceToDestinationDescription

    //the both sides of the relationship i.e. for parentChild this would be child (for synonym this is synonym, so the same on both sides)
    String destinationToSource

    // detailed explanation of the reversed relationship
    String destinationToSourceDescription

    //you can constrain the relationship type
    Class sourceClass

    // you can constrain the relationship type
    Class destinationClass

    // comma separated list of metadata hints
    /**
     * @deprecated use angular metadataEditors instead
     */
    @Deprecated String metadataHints

    // if the direction of the relationship doesn't matter
    Boolean bidirectional = Boolean.FALSE

    /** if relationships of this type shall not be carried over when new draft version is created */
    Boolean versionSpecific = Boolean.FALSE

    SecuredRuleExecutor.ReusableScript ruleScript

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
        sourceToDestinationDescription nullable: true, maxSize: 2000
        destinationToSourceDescription nullable: true, maxSize: 2000
    }

    static transients = ['ruleScript']


    static mapping = {
        cache 'nonstrict-read-write'
        sort "name"
		name index: 'RelationType_name_idx'
		destinationClass index: 'RelationType_destinationClass_idx'
        metadataHints type: 'text'
        rule type: 'text'
        searchable defaultValue: "false"
    }

    void setRule(String rule) {
        this.rule = rule
        this.ruleScript = null
    }

    def validateSourceDestination(CatalogueElement source, CatalogueElement destination, Map<String, String> ext) {
        if (!sourceClass.isInstance(source)) {
            return 'source.not.instance.of'
        }

        if (!destinationClass.isInstance(destination)) {
            return 'destination.not.instance.of'
        }

        if (rule && rule.trim()) {
            def result
            try {
                result = validateRule(source, destination, ext)
            } catch (e) {
                log.warn("Exception validating rule of $this", e)
                result = e
            }
            if (result instanceof List && result.size() > 1 && result.first() instanceof String) {
                return result
            }

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
                log.warn("Rule of $name thrown an exception for $source and $destination: $result", result)
                return ['rule.did.not.pass.with.exception', [result.toString()] as Object[], "Rule thrown an exception: $result.message"]
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

        if (!ruleScript) {
            ruleScript = new SecuredRuleExecutor(RelationshipTypeRuleScript,
                source: source,
                destination: destination,
                type: this,
                ext: ext
            ).reuse(rule)
        }

        ruleScript.execute(
            source: source,
            destination: destination,
            type: this,
            ext: ext
        )
    }


    static RelationshipType getContainmentType() {
        readByName("containment")
    }


    static RelationshipType getInvolvednessType() {
        readByName("involvedness")
    }


    static RelationshipType getRuleContextType() {
        readByName("ruleContext")
    }

    /**
     * @deprecated no longer used, set the data model directly to the CatalogueElement
     */
    static RelationshipType getDeclarationType() {
        readByName("declaration")
    }

    static RelationshipType getFavouriteType() {
        readByName("favourite")
    }

    static RelationshipType getSynonymType() {
        readByName("synonym")
    }

    static RelationshipType getRelatedToType() {
        readByName("relatedTo")
    }

    static RelationshipType getHierarchyType() {
        readByName("hierarchy")
    }

    static RelationshipType getSupersessionType() {
        readByName("supersession")
    }

    static RelationshipType getOriginType() {
        readByName("origin")
    }

    static RelationshipType getBaseType() {
        readByName("base")
    }

    static RelationshipType getImportType() {
        readByName("import")
    }


    static RelationshipType getTagType() {
        readByName("tag")
    }

    static RelationshipType readByName(String name) {
        // TODO: temporary give warning if 'classification' type is requested
        if (name == 'classification') {
            Logger.getLogger(RelationshipType).warn extractFormattedException(new IllegalArgumentException("Relationship 'classification' was replaced by 'definition'. Update your code properly!"))
            return declarationType
        }
        try {
            Long id = CacheService.TYPES_CACHE.get(name, { ->
                RelationshipType type = RelationshipType.findByName(name, [cache: true, readOnly: true])
                if (!type) {
                    throw new IllegalArgumentException("Type '$name' does not exist!")
                }
                return type.id
            } as Callable<Long>)

            RelationshipType type = RelationshipType.get(id)
            if (type) {
                return type
            }
            type = RelationshipType.findByName(name)
            if (type) {
                CacheService.TYPES_CACHE.asMap().put(name, type.id)
                return type
            }
            return null
        } catch (UncheckedExecutionException e) {
            if (e.cause instanceof IllegalArgumentException) {
                Logger.getLogger(RelationshipType).warn "Type '$name' requested but not found!"
            } else {
                throw e
            }
        }
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

    Map<String, Object> getInfo() {
        ImmutableMap.of(
            'id', id,
            'name', name,
            'link', "/${GrailsNameUtils.getPropertyName(getClass())}/$id"
        )
    }

    def beforeInsert() {
        relationshipTypeService.clearCache()
    }

    def afterInsert() {
        CacheService.TYPES_CACHE.put name, getId()
        modelCatalogueSearchService.index(this).subscribe(ErrorSubscriber.create("Exception indexing relationship type after insert"))
    }

    def beforeUpdate() {
        relationshipTypeService.clearCache()
        CacheService.TYPES_CACHE.invalidate(name)
    }

    def afterUpdate() {
        CacheService.TYPES_CACHE.put name, getId()
        modelCatalogueSearchService.index(this).subscribe(ErrorSubscriber.create("Exception indexing relationship type after update"))
    }

    def beforeDelete() {
        relationshipTypeService.clearCache()
        CacheService.TYPES_CACHE.invalidate(name)
        modelCatalogueSearchService.unindex(this).subscribe(ErrorSubscriber.create("Exception unindexing relationship type before delete"))
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

    static String extractFormattedException(Throwable exception) {
        String exceptionMsg = exception.message + "\n"
        StackTraceFilterer stackTraceFilterer = new DefaultStackTraceFilterer()
        exceptionMsg += stackTraceFilterer.filter(exception).stackTrace.join('\n')
        return exceptionMsg
    }
}



