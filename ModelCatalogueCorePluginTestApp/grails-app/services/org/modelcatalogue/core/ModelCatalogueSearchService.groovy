package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.ParamArgs
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller
import rx.Observable
import org.modelcatalogue.core.util.SearchParams

/**
 * Poor man's search service searching in name and description
 * , you should use search service designed for particular search plugin
 */
class ModelCatalogueSearchService implements SearchCatalogue {

    static transactional = false

    def dataModelService
    def modelCatalogueSecurityService
    def elementService

    DataModelGormService dataModelGormService

    @Override
    boolean isIndexingManually() {
        return false
    }

    //Search relationships
    // i.e. search for anything that is a favourite on the home screen

    @Override
    ListWithTotalAndType<Relationship> search(CatalogueElement element,
                                              RelationshipType type,
                                              RelationshipDirection direction,
                                              SearchParams searchParams) {
        String search = searchParams.search
        String status = searchParams.status
        Long dataModelId = searchParams.dataModelId
        ParamArgs paramArgs = searchParams.paramArgs

        String query = "%$params.search%"
        Map params = paramArgs.toMap()
        List<DataModel> subscribedModels = dataModelGormService.findAll()

        DetachedCriteria<Relationship> criteria = direction.composeWhere(element,
                type,
                ElementService.findAllElementStatus(status, modelCatalogueSecurityService.isSubscribed(element)),
                getOverridableDataModelFilter(dataModelId, subscribedModels)
        )

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

    public <T> ListWithTotalAndType<T> search(Class<T> resource,
                                              SearchParams searchParams) {
        String search = searchParams.search
        String status = searchParams.status
        Long dataModelId = searchParams.dataModelId
        ParamArgs paramArgs = searchParams.paramArgs

        Map params = paramArgs.toMap()

        List<DataModel> subscribedModels = dataModelGormService.findAll()

        // if the user doesn't have at least VIEWER role, don't return other elements than finalized
//        if (!params.status && !false /*modelCatalogueSecurityService.hasRole('VIEWER')*/) {
//            params.status = 'FINALIZED'
//        }

        String query = "%${search}%"

        //TODO: check why measurement unit is included here

        if (DataModel.isAssignableFrom(resource) || /* MeasurementUnit.isAssignableFrom(resource) ||*/ Tag.isAssignableFrom(resource)) {
            DetachedCriteria<T> criteria = new DetachedCriteria(resource)
            criteria.or {
                ilike('name', query)
                ilike('description', query)
                ilike('modelCatalogueId', query)
            }

            criteria.'in'('id', subscribedModels.collect{it.id})

            if (status) {
                criteria.'in'('status', ElementService.findAllElementStatus(status, false))
            }
            return Lists.fromCriteria(paramArgs.toMap(), criteria).customize {
                it.collect { item -> CatalogueElementMarshaller.minimalCatalogueElementJSON(item) }
            }
        }

        if (CatalogueElement.isAssignableFrom(resource)) {
            DataModelFilter dataModels = getOverridableDataModelFilter(dataModelId, subscribedModels).withImports(subscribedModels)


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

            if (status) {
                statuses = [ElementStatus.valueOf(status.toUpperCase())]
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
            and {
                'dataModel' in subscribedModels
            }
        }
    }

    ListWithTotalAndType<CatalogueElement> search(SearchParams searchParams) {
        search(CatalogueElement, searchParams)
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

    protected DataModelFilter getOverridableDataModelFilter(Long dataModelId, List<DataModel> subscribedModels) {
        if ( dataModelId ) {
            if( subscribedModels.find { it.id == dataModelId } ) {
                DataModel dataModel = dataModelGormService.findById(dataModelId)
                if (dataModel) {
                    return DataModelFilter.includes(dataModel)
                }
            }
        } else{
            return DataModelFilter.includes(subscribedModels)
        }
        dataModelService.dataModelFilter
    }

    void deleteIndexes(){
        log.info "Using database search, reindexing not needed!"
    }


}
