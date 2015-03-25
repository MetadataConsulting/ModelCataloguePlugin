package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.util.ClassificationFilter
import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.RelationshipDirection

/**
 * Poor man's search service searching in name and description
 * , you should use search service designed for particular search plugin
 */
class ModelCatalogueSearchService implements SearchCatalogue {

    def classificationService

    @Override
    def search(CatalogueElement element, RelationshipType type, RelationshipDirection direction, Map params) {
        String query = "%$params.search%"

        DetachedCriteria<Relationship> criteria = direction.composeWhere(element, type, classificationService.classificationsInUse)

        switch (direction) {
            case RelationshipDirection.OUTGOING:
                criteria.destination {
                    or {
                        ilike('name', query)
                        ilike('description', query)
                    }
                }
                break
            case RelationshipDirection.INCOMING:
                criteria.source {
                    or {
                        ilike('name', query)
                        ilike('description', query)
                    }
                }
                break
            case RelationshipDirection.BOTH:
                criteria.or {
                    destination {
                        or {
                            ilike('name', query)
                            ilike('description', query)
                        }
                        ne('id', element.id)
                    }
                    source {
                        or {
                            ilike('name', query)
                            ilike('description', query)
                        }
                        ne('id', element.id)
                    }

                }
        }

        [searchResults: criteria.list(params), total: criteria.count()]
    }

    def search(Class resource, Map params) {
        if (!params.search) {
            return [errors: "No query string to search on"]
        }
        def searchResults = [:]

        String query = "%$params.search%"

        if (Classification.isAssignableFrom(resource) || MeasurementUnit.isAssignableFrom(resource)) {
            DetachedCriteria criteria = new DetachedCriteria(resource)
            criteria.or {
                ilike('name', query)
                ilike('description', query)
                ilike('modelCatalogueId', query)
            }
            if (params.status) {
                criteria.eq('status', ElementStatus.valueOf(params.status.toString().toUpperCase()))
            }
            searchResults.searchResults = criteria.list(params)
            searchResults.total = criteria.count()
        } else if (CatalogueElement.isAssignableFrom(resource)) {
            ClassificationFilter classifications = classificationService.classificationsInUse

            String alias = resource.simpleName[0].toLowerCase()
            String listQuery = """
                from ${resource.simpleName} ${alias}
                where
                    ${alias}.status in :statuses
                    and (
                        lower(${alias}.name) like lower(:query)
                        or lower(${alias}.description) like lower(:query)
                        or lower(${alias}.modelCatalogueId) like lower(:query)
                        or ${alias} in (select ev.element from ExtensionValue ev where lower(ev.extensionValue) like lower(:query))
                    )
            """

            List<ElementStatus> statuses = [ElementStatus.DRAFT, ElementStatus.PENDING, ElementStatus.UPDATED, ElementStatus.FINALIZED]

            if (params.status) {
                statuses = [ElementStatus.valueOf(params.status.toString().toUpperCase())]
            }

            Map<String, Object> arguments = [
                    query: query,
                    statuses: statuses
            ]

            if (classifications) {
                if (classifications.unclassifiedOnly) {
                    listQuery = """
                    from ${resource.simpleName} ${alias}
                    where
                        ${alias}.status in :statuses
                        and (
                            lower(${alias}.name) like lower(:query)
                            or lower(${alias}.description) like lower(:query)
                            or lower(${alias}.modelCatalogueId) like lower(:query)
                            or ${alias} in (select ev.element from ExtensionValue ev where lower(ev.extensionValue) like lower(:query))
                        )
                        and ${alias} not in (select rel.destination from Relationship rel where rel.relationshipType = :classificationType)
                    """
                    arguments.classificationType = RelationshipType.classificationType
                } else {
                    listQuery = """
                    from ${resource.simpleName} ${alias} left join ${alias}.incomingRelationships as rel
                    where
                        ${alias}.status in :statuses
                        and (
                            lower(${alias}.name) like lower(:query)
                            or lower(${alias}.description) like lower(:query)
                            or lower(${alias}.modelCatalogueId) like lower(:query)
                            or ${alias} in (select ev.element from ExtensionValue ev where lower(ev.extensionValue) like lower(:query))
                        )
                        and rel.relationshipType = :classificationType
                    """

                    if (classifications.includes) {
                        listQuery += " and rel.source.id in :includes "
                        arguments.includes = classifications.includes
                    }

                    if (classifications.excludes) {
                        listQuery += " and rel.source.id not in :excludes "
                        arguments.excludes = classifications.excludes
                    }
                    arguments.classificationType = RelationshipType.classificationType
                }
            }

            def results = Lists.fromQuery(params, resource, listQuery, arguments)


            searchResults.searchResults = results.items
            searchResults.total = results.total
        } else if (RelationshipType.isAssignableFrom(resource)) {
            searchResults.searchResults = resource.findAllByNameIlikeOrSourceToDestinationIlikeOrDestinationToSourceIlike(query, query, query, params)
            searchResults.total = resource.countByNameIlikeOrSourceToDestinationIlikeOrDestinationToSourceIlike(query, query, query)
        } else {
            searchResults.searchResults = resource.findAllByNameIlike(query, params)
            searchResults.total         = resource.countByNameIlike(query)
        }

        searchResults
    }

    def search(Map params){
        search CatalogueElement, params
    }

    def index(Class resource){}
    def index(Collection<Class> resource){}
    def unindex(Object object){}
    def unindex(Collection<Object> object){}
    def refresh(){}

}
