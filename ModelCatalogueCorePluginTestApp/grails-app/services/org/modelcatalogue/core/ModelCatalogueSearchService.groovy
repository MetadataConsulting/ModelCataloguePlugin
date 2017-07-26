package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller
import rx.Observable

/**
 * Poor man's search service searching in name and description
 * , you should use search service designed for particular search plugin
 */
class ModelCatalogueSearchService implements SearchCatalogue {

    static transactional = false

    def dataModelService
    def modelCatalogueSecurityService
    def elementService

    @Override
    boolean isIndexingManually() {
        return false
    }

    @Override
    ListWithTotalAndType<Relationship> search(CatalogueElement element, RelationshipType type, RelationshipDirection direction, Map params) {
        String query = "%$params.search%"

        DetachedCriteria<Relationship> criteria = direction.composeWhere(element, type, ElementService.getStatusFromParams(params, false /*modelCatalogueSecurityService.hasRole('VIEWER')*/), getOverridableDataModelFilter(params))

        if (query != '%*%') {
            switch (direction) {
                case RelationshipDirection.INCOMING:
                    criteria.source {
                        or {
                            ilike('name', query)
                            ilike('description', query)
                        }
                    }
                    break
                default:
                    criteria.destination {
                        or {
                            ilike('name', query)
                            ilike('description', query)
                        }
                    }
                    break
            }
        }
        return Lists.fromCriteria(params, criteria)
    }

    public <T> ListWithTotalAndType<T> search(Class<T> resource, Map params) {
        // if the user doesn't have at least VIEWER role, don't return other elements than finalized
        if (!params.status && !false /*modelCatalogueSecurityService.hasRole('VIEWER')*/) {
            params.status = 'FINALIZED'
        }

        String query = "%$params.search%"

        if (DataModel.isAssignableFrom(resource) || MeasurementUnit.isAssignableFrom(resource) || Tag.isAssignableFrom(resource)) {
            DetachedCriteria<T> criteria = new DetachedCriteria(resource)
            criteria.or {
                ilike('name', query)
                ilike('description', query)
                ilike('modelCatalogueId', query)
            }
            if (params.status) {
                criteria.'in'('status', ElementService.getStatusFromParams(params, false /*modelCatalogueSecurityService.hasRole('VIEWER')*/))
            }
            return Lists.fromCriteria(params, criteria).customize {
                it.collect { item -> CatalogueElementMarshaller.minimalCatalogueElementJSON(item) }
            }
        }

        if (CatalogueElement.isAssignableFrom(resource)) {
            DataModelFilter dataModels = getOverridableDataModelFilter(params).withImports()

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

            if (dataModels) {
                if (dataModels.unclassifiedOnly) {
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
                        and ${alias}.dataModel is null
                    """
                } else {
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
                    """

                    if (dataModels.includes) {
                        listQuery += " and ${alias}.dataModel.id in :includes "
                        arguments.includes = dataModels.includes
                    }

                    if (dataModels.excludes) {
                        listQuery += " and r${alias}.dataModel.id not in :excludes "
                        arguments.excludes = dataModels.excludes
                    }

                }
            }

            return Lists.fromQuery(params, resource, listQuery, arguments).customize {
                it.collect { element -> CatalogueElementMarshaller.minimalCatalogueElementJSON(element as CatalogueElement) }
            }
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

    Observable<Boolean> reindex(boolean soft)  {
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
