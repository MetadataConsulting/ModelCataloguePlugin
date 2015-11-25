package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.RelationshipDirection
import rx.Observable

/**
 * Poor man's search service searching in name and description
 * , you should use search service designed for particular search plugin
 */
class ModelCatalogueSearchService implements SearchCatalogue {

    static transactional = false

    def dataModelService
    def modelCatalogueSecurityService

    @Override
    boolean isIndexingManually() {
        return false
    }

    @Override
    ListWithTotalAndType<Relationship> search(CatalogueElement element, RelationshipType type, RelationshipDirection direction, Map params) {
        String query = "%$params.search%"

        DetachedCriteria<Relationship> criteria = direction.composeWhere(element, type, getOverridableDataModelFilter(params))

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

        return Lists.fromCriteria(params, criteria)
    }

    public <T> ListWithTotalAndType<T> search(Class<T> resource, Map params) {
        // if the user doesn't have at least VIEWER role, don't return other elements than finalized
        if (!params.status && !modelCatalogueSecurityService.hasRole('VIEWER')) {
            params.status = 'FINALIZED'
        }

        String query = "%$params.search%"

        if (DataModel.isAssignableFrom(resource) || MeasurementUnit.isAssignableFrom(resource)) {
            DetachedCriteria<T> criteria = new DetachedCriteria(resource)
            criteria.or {
                ilike('name', query)
                ilike('description', query)
                ilike('modelCatalogueId', query)
            }
            if (params.status) {
                criteria.eq('status', ElementStatus.valueOf(params.status.toString().toUpperCase()))
            }
            return Lists.fromCriteria(params, criteria)
        }

        if (CatalogueElement.isAssignableFrom(resource)) {
            DataModelFilter classifications = getOverridableDataModelFilter(params).withImports()

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
                    arguments.classificationType = RelationshipType.declarationType
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
                    arguments.classificationType = RelationshipType.declarationType
                }
            }

            return Lists.fromQuery(params, resource, listQuery, arguments)
        }

        if (RelationshipType.isAssignableFrom(resource)) {
            return Lists.fromCriteria(params, resource) {
                or {
                    ilike 'name', query
                    ilike 'sourceToDestination', query
                    ilike 'destinationToSource', query
                }
            }

        }

        return Lists.fromCriteria(params, resource) {
            or {
                ilike 'name', query
            }
        }
    }

    ListWithTotalAndType<CatalogueElement> search(Map params){
        search CatalogueElement, params
    }

    Observable<Boolean> index(Object element) {
        Observable.just(true)
    }


    Observable<Boolean> index(Iterable<Object> resource)  {
        Observable.just(true)
    }

    Observable<Boolean> unindex(Object object)  {
        Observable.just(true)
    }

    Observable<Boolean> unindex(Collection<Object> object)  {
        Observable.just(true)
    }

    Observable<Boolean> reindex()  {
        log.info "Using database search, reindexing not needed!"
        Observable.just(true)
    }

    protected DataModelFilter getOverridableDataModelFilter(Map params) {
        if (params.dataModel) {
            DataModel dataModel = DataModel.get(params.long('dataModel'))
            if (dataModel) {
                return DataModelFilter.includes(dataModel)
            }
        }
        dataModelService.dataModelFilter
    }

}
