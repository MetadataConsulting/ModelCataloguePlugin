package org.modelcatalogue.core

import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.modelcatalogue.core.persistence.RelationshipTypeGormService

class RelationshipTypeService {

    RelationshipTypeGormService relationshipTypeGormService

    static transactional = false

    private  Map<Class, Map<String, RelationshipType>> typesForDomainClassCache = [:]
    private Map<String, RelationshipType> relationshipTypesCache = [:]

    Map<String, RelationshipType> getAllRelationshipTypes() {
        if (relationshipTypesCache) {
            return relationshipTypesCache
        }
        List<RelationshipType> relationshipTypeList = relationshipTypeGormService.findRelationshipTypes()
        relationshipTypesCache = relationshipTypeList.collectEntries { [it.name, it] }
    }

    /**
     * named map of relationship types which a class is involved in.
     * @param cls
     * @return
     */
    Map<String, RelationshipType> getRelationshipTypesFor(Class cls) {
        Map<String, RelationshipType> types = typesForDomainClassCache[cls]

        if (types) return types

        types = allRelationshipTypes.findAll { name, type ->
            type.sourceClass.isAssignableFrom(cls) || type.destinationClass.isAssignableFrom(cls)
        }

        typesForDomainClassCache[cls] = types

        types
    }

    void clearCache() {
        relationshipTypesCache.clear()
        typesForDomainClassCache.clear()
    }

    /**
     * e.g. relationships = {
     *     "incoming": {
     *         "hierarchy": "childOf"
     *     }
     *     "outgoing": {
     *         "hierarchy": "parentOf"
     *     }
     *     "bidirectional": {
     *         "relatedTo": "relatedTo"
     *     }
     *  }
     * @param type
     * @return
     */
    Map<String, Map<String, String>> getRelationshipConfiguration(Class type) {

        Map<String, Map<String, String>> relationships  = [incoming: [:], outgoing: [:], bidirectional: [:]]

        if (type.superclass && CatalogueElement.isAssignableFrom(type.superclass)) { // recursively get relationships from superclasses
            Map<String, Map<String, String>> fromSuperclass = getRelationshipConfiguration(type.superclass)
            relationships.incoming.putAll(fromSuperclass.incoming ?: [:])
            relationships.outgoing.putAll(fromSuperclass.outgoing ?: [:])
            relationships.bidirectional.putAll(fromSuperclass.bidirectional ?: [:])
        }

        // get relationships from this class
        Map<String, Map<String, String>> fromType = GrailsClassUtils.getStaticFieldValue(type, 'relationships') ?: [incoming: [:], outgoing: [:], bidirectional: [:]]
        relationships.incoming.putAll(fromType.incoming ?: [:])
        relationships.outgoing.putAll(fromType.outgoing ?: [:])
        relationships.bidirectional.putAll(fromType.bidirectional ?: [:])

        getRelationshipTypesFor(type).each { String name, RelationshipType relationshipType ->
            if (relationshipType.system) {
                relationships.each { String direction, Map config ->
                    config.remove name
                }
                return
            }

            if (relationshipType.bidirectional) {
                if (!relationships.bidirectional.containsKey(name)) {
                    relationships.incoming.remove(name)
                    relationships.outgoing.remove(name)

                    relationships.bidirectional[name] = RelationshipType.toCamelCase(relationshipType.sourceToDestination)
                }
            } else {
                if (relationshipType.sourceClass.isAssignableFrom(type)) {
                    if (!relationships.outgoing.containsKey(name)){
                        relationships.bidirectional.remove(name)

                        relationships.outgoing[name] = RelationshipType.toCamelCase(relationshipType.sourceToDestination)
                    }
                }
                if (relationshipType.destinationClass.isAssignableFrom(type)) {
                    if (!relationships.incoming.containsKey(name)) {

                        relationships.bidirectional.remove(name)

                        relationships.incoming[name] = RelationshipType.toCamelCase(relationshipType.destinationToSource)
                    }
                }
            }
        }


        relationships
    }


}
