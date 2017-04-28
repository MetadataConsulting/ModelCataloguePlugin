package org.modelcatalogue.core.util

import groovy.util.logging.Log
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType

/**
 * Defines among other things dynamic  methods for getting an element's relationship
 * by type name.
 * Created by ladin on 24.03.14.
 */
@Log
class CatalogueElementDynamicHelper {

    static void addShortcuts(Class<? extends CatalogueElement>... types) {
        types.each { addShortcuts(it) }
    }

    /**
     * So this is where the static relationships of a CatalogueElement is used!
     * @param type
     */
    static void addShortcuts(Class<? extends CatalogueElement> type) {
        if (type != CatalogueElement && type.superclass != CatalogueElement) addShortcuts(type.superclass)
        def transients      = GrailsClassUtils.getStaticFieldValue(type, 'transients')      ?: []
        def relationships   = GrailsClassUtils.getStaticFieldValue(type, 'relationships')   ?: [:]

        relationships.incoming?.each        handleRelationships(type, 'incoming', transients)
        relationships.outgoing?.each        handleRelationships(type, 'outgoing', transients)
        relationships.bidirectional?.each   handleRelationships(type, '', transients)

        type.transients = new LinkedHashSet(transients).toList()
    }

    private static Closure handleRelationships(Class type, String direction, transients) {
        { String relName, String name ->
            transients << name
            if (!type.metaClass.hasProperty(name)) {
                type.metaClass."get${name.capitalize()}" = {->
                    RelationshipType relType = RelationshipType.readByName(relName)
                    if (!relType) {
                        log.warning "querying for $name for $delegate.name but the relationship type $relName does not exist"
                        return []
                    }
                    if (!delegate.id) {
                        log.info "trying to get relations on transient object, returning empty list"
                        return []
                    }
                    delegate."get${direction.capitalize()}RelationsByType"(relType)
                }
                type.metaClass."get${name.capitalize()}Relationships" = {->
                    RelationshipType relType = RelationshipType.readByName(relName)
                    if (!relType) {
                        log.warning "querying for $name for $delegate.name but the relationship type $relName does not exist"
                        return []
                    }
                    if (!delegate.id) {
                        log.info "trying to get relations on transient object, returning empty list"
                        return []
                    }
                    delegate."get${direction.capitalize()}RelationshipsByType"(relType)
                }
            }
            String countMethodName = "count${name.capitalize()}"
            if (!type.metaClass.metaMethods.any {it.name == countMethodName}) {
                type.metaClass[countMethodName] = {->
                    RelationshipType relType = RelationshipType.readByName(relName)
                    if (!relType) {
                        log.warning "querying count of $name for $delegate.name but the relationship type $relName does not exist"
                        return 0
                    }
                    delegate."count${direction.capitalize()}RelationshipsByType"(relType)
                }
            }

            String addMethodName = "addTo${name.capitalize()}"
            if (!type.metaClass.metaMethods.any {it.name == addMethodName}) {
                type.metaClass[addMethodName] = { Map<String, Object> params = [:], CatalogueElement other ->
                    RelationshipType relType = RelationshipType.readByName(relName)
                    if (!relType) throw new IllegalArgumentException("Unknown relationship type $relName")
                    Relationship rel = delegate."createLink${direction == 'incoming' ? 'From' : 'To'}"(params, other, relType)
                    if (rel.hasErrors()) {
                        throw new IllegalArgumentException(FriendlyErrors.printErrors("Cannot create relation '$delegate.name' ${direction == 'incoming' ? relType.destinationToSource : relType.sourceToDestination} '$other.name'", rel.errors))
                    }
                    rel
                }
            }

            String removeMethodName = "removeFrom${name.capitalize()}"
            if (!type.metaClass.metaMethods.any {it.name == removeMethodName}) {
                type.metaClass[removeMethodName] = {other ->
                    RelationshipType relType = RelationshipType.readByName(relName)
                    if (!relType) throw new IllegalArgumentException("Unknown relationship type $relType")
                    delegate."removeLink${direction == 'incoming' ? 'From' : 'To'}"(other, relType)
                }
            }

        }
    }

}
