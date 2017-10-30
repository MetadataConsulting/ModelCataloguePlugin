package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import groovy.transform.CompileStatic
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.events.CatalogueElementNotFoundEvent
import org.modelcatalogue.core.events.MetadataResponseEvent
import org.modelcatalogue.core.events.RelationshipTypeNotFoundEvent
import org.modelcatalogue.core.events.RelationshipsEvent
import org.modelcatalogue.core.persistence.CatalogueElementGormService
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.ParamArgs
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.ListWrapper
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.lists.Relationships

@CompileStatic
class RelationshipsInternalService {

    CatalogueElementGormService catalogueElementGormService

    MetadataResponseEvent relationshipsInternal(Long catalogueElementId,
                                                String typeParam,
                                                RelationshipDirection direction,
                                                String resourceName,
                                                ParamArgs paramsArgs,
                                                DataModelFilter overridableDataModelFilter) {
        CatalogueElement element = catalogueElementGormService.findById(catalogueElementId)
        if (!element) {
            return new CatalogueElementNotFoundEvent()
        }

        RelationshipType type = typeParam ? RelationshipType.readByName(typeParam) : null
        if (typeParam && !type) {
            return new RelationshipTypeNotFoundEvent()
        }
        Map params = paramsArgs.toMap()
        List<ElementStatus> elementStatusList = ElementService.getStatusFromParams(params, true)
        DetachedCriteria criteria = direction.composeWhere(element, type, elementStatusList, overridableDataModelFilter)
        String base = base(catalogueElementId, typeParam, resourceName, direction)
        ListWrapper<Relationship> list = Lists.fromCriteria(params, base, criteria)
        Relationships relationships = new Relationships(type: type, owner: element, direction: direction, list: list)
        new RelationshipsEvent(relationships: relationships)
    }

    String base(Long catalogueElementId,  String typeParam, String resourceName, RelationshipDirection direction) {
        String suffix = typeParam ? "/${typeParam}" : ''
        "/${resourceName}/${catalogueElementId}/${direction.actionName}${suffix}"
    }
}
