package org.modelcatalogue.core.util

import groovy.util.logging.Log
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.RelationshipType

/**
 * Created by ladin on 24.03.14.
 */
@Log
class CatalogueElementDynamicHelper {

    static void addShortcuts(Class<? extends CatalogueElement>... types) {
        types.each { addShortcuts(it) }
    }

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
                    RelationshipType relType = RelationshipType.findByName(relName)
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
            }
            String countMethodName = "count${name.capitalize()}"
            if (!type.metaClass.metaMethods.any {it.name == countMethodName}) {
                type.metaClass[countMethodName] = {->
                    RelationshipType relType = RelationshipType.findByName(relName)
                    if (!relType) {
                        log.warning "querying count of $name for $delegate.name but the relationship type $relName does not exist"
                        return 0
                    }
                    delegate."count${direction.capitalize()}RelationsByType"(relType)
                }
            }

            String addMethodName = "addTo${name.capitalize()}"
            if (!type.metaClass.metaMethods.any {it.name == addMethodName}) {
                type.metaClass[addMethodName] = { other ->
                    RelationshipType relType = RelationshipType.findByName(relName)
                    if (!relType) throw new IllegalArgumentException("Unknown relationship type $relName")
                    delegate."createLink${direction == 'incoming' ? 'From' : 'To'}"(other, relType)
                }
            }

            String removeMethodName = "removeFrom${name.capitalize()}"
            if (!type.metaClass.metaMethods.any {it.name == removeMethodName}) {
                type.metaClass[removeMethodName] = {other ->
                    RelationshipType relType = RelationshipType.findByName(relName)
                    if (!relType) throw new IllegalArgumentException("Unknown relationship type $relType")
                    delegate."removeLink${direction == 'incoming' ? 'From' : 'To'}"(other, relType)
                }
            }

        }
    }

}
