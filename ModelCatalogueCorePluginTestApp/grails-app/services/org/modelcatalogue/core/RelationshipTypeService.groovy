package org.modelcatalogue.core

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
