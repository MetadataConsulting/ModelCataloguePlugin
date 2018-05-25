package org.modelcatalogue.core

import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.modelcatalogue.core.persistence.RelationshipTypeGormService
import org.modelcatalogue.core.util.RelationshipDirection

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

    RelationshipConfiguration getRelationshipConfigurationRec(Class type) {

        RelationshipConfiguration relationshipConfiguration = new RelationshipConfiguration()

        if (type.superclass && CatalogueElement.isAssignableFrom(type.superclass)) { // recursively get relationships from superclasses
            RelationshipConfiguration fromSuperclass = getRelationshipConfigurationRec(type.superclass)
            relationshipConfiguration.add(fromSuperclass)
        }

        // get relationships from this class
        Map<String, Map<String, String>> fromType = GrailsClassUtils.getStaticFieldValue(type, 'relationships') ?: [incoming: [:], outgoing: [:], bidirectional: [:]]
        relationshipConfiguration.add(fromType)

        return relationshipConfiguration
    }
    /**
     *
     * @param type
     * @return
     */
    RelationshipConfiguration getRelationshipConfiguration(Class type) {
        RelationshipConfiguration relationshipConfiguration  = getRelationshipConfigurationRec(type)

        // filter by database stuff
        getRelationshipTypesFor(type).each { String name, RelationshipType relationshipType ->
            if (relationshipType.system) {
                relationshipConfiguration.each {it.remove name}

                return // go to next part of each loop
            }

            if (relationshipType.bidirectional) {
                if (!relationshipConfiguration.bidirectional.containsKey(name)) {
                    relationshipConfiguration.incoming.remove(name)
                    relationshipConfiguration.outgoing.remove(name)

                    relationshipConfiguration.bidirectional[name] = RelationshipType.toCamelCase(relationshipType.sourceToDestination)
                }
            } else {
                if (relationshipType.sourceClass.isAssignableFrom(type)) {
                    if (!relationshipConfiguration.outgoing.containsKey(name)){
                        relationshipConfiguration.bidirectional.remove(name)

                        relationshipConfiguration.outgoing[name] = RelationshipType.toCamelCase(relationshipType.sourceToDestination)
                    }
                }
                if (relationshipType.destinationClass.isAssignableFrom(type)) {
                    if (!relationshipConfiguration.incoming.containsKey(name)) {

                        relationshipConfiguration.bidirectional.remove(name)

                        relationshipConfiguration.incoming[name] = RelationshipType.toCamelCase(relationshipType.destinationToSource)
                    }
                }
            }
        }


        relationshipConfiguration
    }

    /**
    * e.g. ["parentOf": [R1, R2...], "childOf": [R3, R4...]]
    * @param element
    * @param relationshipConfiguration
    * @return
    */
    List<RelationshipInfo> relationshipInfoOf(CatalogueElement element, RelationshipConfiguration relationshipConfiguration) {
        List<RelationshipInfo> result = []

        // incoming
        Map<String,String> incoming = relationshipConfiguration.incoming
        incoming.each {relationshipName, directionalRelationshipName ->
            result.add(new RelationshipInfo(
                directionalRelationshipName: directionalRelationshipName,
                relationships: element.getIncomingRelationshipsByType(RelationshipType.readByName(relationshipName)), // initialize empty list
                relationshipDirection: RelationshipDirection.INCOMING


            ))
        }

        // outgoing

        Map<String, String> outgoing = relationshipConfiguration.outgoing
        outgoing.each {relationshipName, directionalRelationshipName ->
            result.add(new RelationshipInfo(
                directionalRelationshipName: directionalRelationshipName,
                relationships: element.getOutgoingRelationshipsByType(RelationshipType.readByName(relationshipName)), // initialize empty list
                relationshipDirection: RelationshipDirection.OUTGOING


            ))

        }


        // bidirectional
        Map<String, String> bidirectional = relationshipConfiguration.bidirectional
        bidirectional.each {relationshipName, directionalRelationshipName ->
            result.add(new RelationshipInfo(
                directionalRelationshipName: directionalRelationshipName,
                relationships: element.getOutgoingRelationshipsByType(RelationshipType.readByName(relationshipName)), // initialize empty list
                relationshipDirection: RelationshipDirection.OUTGOING


            ))
            // as if outgoing, for bidirectional
        }

        return result
    }




}

class RelationshipInfo {
    String directionalRelationshipName
    List<Relationship> relationships
    RelationshipDirection relationshipDirection

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
 */
class RelationshipConfiguration {
    Map<String, String> incoming = [:]
    Map<String, String> outgoing = [:]
    Map<String, String> bidirectional = [:]

    /**
     * Mutates state
     * @param relationshipConfiguration
     * @return
     */
    RelationshipConfiguration add(RelationshipConfiguration relationshipConfiguration) {
        this.incoming << relationshipConfiguration.incoming ?: [:]
        this.outgoing << relationshipConfiguration.outgoing ?: [:]
        this.bidirectional << relationshipConfiguration.bidirectional ?: [:]
        return this
    }
    /**
     * Mutates state
     * @param fromType
     * @return
     */
    RelationshipConfiguration add(Map<String, Map<String, String>> fromType) {

        this.incoming.putAll(fromType.incoming ?: [:])
        this.outgoing.putAll(fromType.outgoing ?: [:])
        this.bidirectional.putAll(fromType.bidirectional ?: [:])
        return this
    }

    /**
     * Closure c applies to Map<String,String>
     * @param c
     */
    void each(Closure c) {
        c(incoming)
        c(outgoing)
        c(bidirectional)
    }
}
