package org.modelcatalogue.core

class RelationshipTypeService {

    static transactional = false

    private  Map<Class, Map<String, RelationshipType>> typesForDomainClassCache = [:]
    private Map<String, RelationshipType> relationshipTypesCache = [:]

    Map<String, RelationshipType> getAllRelationshipTypes() {
        if (relationshipTypesCache) {
            return relationshipTypesCache
        }

        relationshipTypesCache = RelationshipType.list().collectEntries { [it.name, it]}
    }

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



}
