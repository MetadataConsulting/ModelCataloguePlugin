package org.modelcatalogue.core.catalogueelement.searchwithinrelationships

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ModelCatalogueSearchService
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.events.CatalogueElementNotFound
import org.modelcatalogue.core.events.MetadataResponseEvent
import org.modelcatalogue.core.events.RelationshipTypeNotFoundEvent
import org.modelcatalogue.core.events.RelationshipsEvent
import org.modelcatalogue.core.util.SearchParams
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.lists.Relationships

@CompileStatic
abstract class AbstractSearchWithinRelationshipsService {

    ModelCatalogueSearchService modelCatalogueSearchService

    abstract protected String resourceName()

    abstract protected CatalogueElement findById(Long id)

    MetadataResponseEvent searchWithinRelationships(Long catalogueElementId,
                                                    String type,
                                                    RelationshipDirection direction,
                                                    SearchParams searchParams) {

        CatalogueElement element = findById(catalogueElementId)
        if (!element) {
            return new CatalogueElementNotFound()
        }

        RelationshipType relationshipType = RelationshipType.readByName(type)
        if (!element) {
            return new RelationshipTypeNotFoundEvent()
        }

        Map params = searchParams.paramArgs as Map
        ListWithTotalAndType<Relationship> results = modelCatalogueSearchService.search(element, relationshipType, direction, searchParams)
        String searchEncoded = URLEncoder.encode(search, 'UTF-8')
        String base = "/${resourceName()}/${catalogueElementId}/${direction.actionName}" + (type ? "/${type}" : "") + "/search?search=${searchEncoded ?: ''}"
        Relationships relationships = new Relationships(owner: element,
                direction: direction,
                type: relationshipType,
                list: Lists.wrap(params, base, results))
        new RelationshipsEvent(relationships: relationships)
    }
}
