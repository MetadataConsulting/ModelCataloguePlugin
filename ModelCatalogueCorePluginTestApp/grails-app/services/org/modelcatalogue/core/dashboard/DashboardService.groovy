package org.modelcatalogue.core.dashboard

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MaxOffsetSublistUtils
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.RelationshipTypeName
import org.modelcatalogue.core.Tag
import org.modelcatalogue.core.ValidationRule
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.persistence.AssetGormService
import org.modelcatalogue.core.persistence.DataClassGormService
import org.modelcatalogue.core.persistence.DataElementGormService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.persistence.DataTypeGormService
import org.modelcatalogue.core.persistence.EnumeratedTypeGormService
import org.modelcatalogue.core.persistence.MeasurementUnitGormService
import org.modelcatalogue.core.persistence.RelationshipGormService
import org.modelcatalogue.core.persistence.RelationshipTypeGormService
import org.modelcatalogue.core.persistence.TagGormService
import org.modelcatalogue.core.persistence.ValidationRuleGormService
import org.modelcatalogue.core.search.KeywordMatchType
import org.modelcatalogue.core.search.KeywordsService
import org.modelcatalogue.core.search.PagedResultList
import org.modelcatalogue.core.util.IdName
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.PublishedStatus
import org.modelcatalogue.core.util.SortQuery
import org.modelcatalogue.core.view.AssetViewModel
import org.modelcatalogue.core.view.CatalogueElementViewModel
import org.modelcatalogue.core.view.CatalogueElementViewModelUtils
import org.modelcatalogue.core.view.DataModelViewModel
import org.modelcatalogue.core.view.DataModelViewModelUtils

@Slf4j
@CompileStatic
class DashboardService {

    DataModelGormService dataModelGormService
    DataElementGormService dataElementGormService
    DataClassGormService dataClassGormService
    AssetGormService assetGormService
    EnumeratedTypeGormService enumeratedTypeGormService
    DataTypeGormService dataTypeGormService
    MeasurementUnitGormService measurementUnitGormService
    ValidationRuleGormService validationRuleGormService
    TagGormService tagGormService
    RelationshipTypeGormService relationshipTypeGormService
    RelationshipGormService relationshipGormService
    KeywordsService keywordsService

    @Transactional(readOnly = true)
    @CompileDynamic
    List<Long> findDataModelDestinationIdsByDataModelAndRelationshipTypeName(Long dataModelId, RelationshipTypeName typeName) {
        RelationshipType relationshipType = relationshipTypeGormService.findByRelationshipTypeName(typeName)
        if (!relationshipType) {
            log.error('Unable to find {} relationship type', typeName.name)
            return [] as List<Long>
        }

        DetachedCriteria<Relationship> relationshipQuery = relationshipGormService.findQueryByRelationshipTypeAndSource(relationshipType, CatalogueElement.get(dataModelId))
        relationshipQuery.join('destination')
        relationshipQuery.projections {
            property('destination')
        }
        def results = relationshipQuery.list()

        if ( !results ) {
            return [] as List<Long>
        }
        DataModel.where {
            id in results*.id
        }.id().list()
    }

    List<Long> dataModelIdsBySearchQuery(SearchQuery query) {
        List<Long> dataModelIds = []
        if ( query?.dataModelId ) {
            dataModelIds << query.dataModelId
            if ( query.searchScope == SearchScope.DATAMODEL_AND_IMPORTS ) {
                dataModelIds += findDataModelDestinationIdsByDataModelAndRelationshipTypeName(query.dataModelId, RelationshipTypeName.IMPORT)
            }
        }
        dataModelIds
    }

    CatalogueElementSearchResult search(SearchQuery query, SortQuery sortQuery = null, PaginationQuery paginationQuery = null) {
        if ( query.metadataDomain == MetadataDomain.DATA_MODEL ) {
            return findAllDataModelViewBySearchStatusQuery(query, sortQuery, paginationQuery)

        } else if ( query.metadataDomain == MetadataDomain.CATALOGUE_ELEMENT ) {
            return findAllCatalogueElementViewBySearchStatusQuery(query, sortQuery, paginationQuery)

        } else if ( query.metadataDomain == MetadataDomain.DATA_ELEMENT ) {
            return findAllDataElementViewBySearchStatusQuery(query, sortQuery, paginationQuery)

        } else if ( query.metadataDomain == MetadataDomain.DATA_CLASS ) {
            return findAllDataClassViewBySearchStatusQuery(query, sortQuery, paginationQuery)

        } else if ( query.metadataDomain == MetadataDomain.ENUMERATED_TYPE ) {
            return findAllEnumeratedTypeViewBySearchStatusQuery(query, sortQuery, paginationQuery)

        } else if ( query.metadataDomain == MetadataDomain.DATA_TYPE ) {
            return findAllDataTypeViewBySearchStatusQuery(query, sortQuery, paginationQuery)

        } else if ( query.metadataDomain == MetadataDomain.MEASUREMENT_UNIT ) {
            return findAllMeasurementUnitViewBySearchStatusQuery(query, sortQuery, paginationQuery)

        } else if ( query.metadataDomain == MetadataDomain.BUSINESS_RULE) {
            return findAllBusinessRuleViewBySearchStatusQuery(query, sortQuery, paginationQuery)

        } else if ( query.metadataDomain == MetadataDomain.TAG) {
            return findAllTagViewBySearchStatusQuery(query, sortQuery, paginationQuery)
        }
        new CatalogueElementSearchResult(total: 0, viewModels: [])
    }

    @CompileDynamic
    PagedResultList resultsOfBuildableCriteriaBySearchStatusQuery(Class clazz,
                                                                  SearchQuery query,
                                                                  SortQuery sortQuery,
                                                                  PaginationQuery paginationQuery) {
        List<Long> dataModelIds = dataModelIdsBySearchQuery(query)
        Map params = paginationQuery?.toMap() ?: [:]

        List<String> keywords = keywordsService.keywords(query.search)
        log.debug('searching keywords: {}', keywords.join(','))
        KeywordMatchType keywordMatchType = query.keywordMatchType
        if ( keywords.size() == 1) {
            keywordMatchType = KeywordMatchType.EXACT_MATCH
        }

        Closure searchQuery = {
            if (query?.search) {
                if (keywordMatchType == KeywordMatchType.EXACT_MATCH) {
                    or {
                        if ( shouldSearchProperty('name', query.searchCatalogueElementScopeList) ) {
                            ilike("name", "%${query.search}%")
                        }
                        if ( shouldSearchProperty('modelCatalogueId', query.searchCatalogueElementScopeList) ) {
                            ilike("modelCatalogueId", "%${query.search}%")
                        }
                        if ( shouldSearchProperty('description', query.searchCatalogueElementScopeList) ) {
                            ilike("description", "%${query.search}%")
                        }
                        if ( shouldSearchProperty('extensions.name', query.searchCatalogueElementScopeList) || shouldSearchProperty('extensions.extensionValue', query.searchCatalogueElementScopeList) ) {
                            extensions {
                                or {
                                    if (shouldSearchProperty('extensions.name', query.searchCatalogueElementScopeList)) {
                                        ilike("name", "%${query.search}%")
                                    }
                                    if (shouldSearchProperty('extensions.extensionValue', query.searchCatalogueElementScopeList)) {
                                        ilike("extensionValue", "%${query.search}%")
                                    }
                                }
                            }
                        }
                    }
                } else if (keywordMatchType == KeywordMatchType.KEYWORDS_MATCH) {
                    or {
                        if (shouldSearchProperty('name', query.searchCatalogueElementScopeList)) {
                            and {
                                for ( String keyword : keywords ) {
                                    ilike("name", "%${keyword}%")
                                }
                            }
                        }
                        if (shouldSearchProperty('modelCatalogueId', query.searchCatalogueElementScopeList)) {
                            and {
                                for ( String keyword : keywords ) {
                                    ilike("modelCatalogueId", "%${keyword}%")
                                }
                            }
                        }
                        if (shouldSearchProperty('description', query.searchCatalogueElementScopeList)) {
                            and {
                                for ( String keyword : keywords ) {
                                    ilike("description", "%${keyword}%")
                                }
                            }
                        }
                        if (shouldSearchProperty('extensions.name', query.searchCatalogueElementScopeList) || shouldSearchProperty('extensions.extensionValue', query.searchCatalogueElementScopeList)) {
                            extensions {
                                or {
                                    if (shouldSearchProperty('extensions.name', query.searchCatalogueElementScopeList)) {
                                        and {
                                            for ( String keyword : keywords ) {
                                                ilike("name", "%${keyword}%")
                                            }
                                        }
                                    }
                                    if (shouldSearchProperty('extensions.extensionValue', query.searchCatalogueElementScopeList)) {
                                        and {
                                            for ( String keyword : keywords ) {
                                                ilike("extensionValue", "%${keyword}%")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (keywordMatchType == KeywordMatchType.BROAD_MATCH) {
                    or {
                        if (shouldSearchProperty('name', query.searchCatalogueElementScopeList)) {
                            or {
                                for ( String keyword : keywords ) {
                                    ilike("name", "%${keyword}%")
                                }
                            }
                        }
                        if (shouldSearchProperty('modelCatalogueId', query.searchCatalogueElementScopeList)) {
                            or {
                                for ( String keyword : keywords ) {
                                    ilike("modelCatalogueId", "%${keyword}%")
                                }
                            }
                        }
                        if (shouldSearchProperty('description', query.searchCatalogueElementScopeList)) {
                            or {
                                for ( String keyword : keywords ) {
                                    ilike("description", "%${keyword}%")
                                }
                            }
                        }
                        if (shouldSearchProperty('extensions.name', query.searchCatalogueElementScopeList) || shouldSearchProperty('extensions.extensionValue', query.searchCatalogueElementScopeList)) {
                            extensions {
                                or {
                                    if (shouldSearchProperty('extensions.name', query.searchCatalogueElementScopeList)) {
                                        or {
                                            for ( String keyword : keywords ) {
                                                ilike("name", "%${keyword}%")
                                            }
                                        }
                                    }
                                    if (shouldSearchProperty('extensions.extensionValue', query.searchCatalogueElementScopeList)) {
                                        or {
                                            for ( String keyword : keywords ) {
                                                ilike("extensionValue", "%${keyword}%")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Closure statusQuery = {
            if (query?.statusList) {
                inList("status", query.statusList)
            }
        }

        Closure dataModelQuery = {
            createAlias('dataModel', 'dataModel')
            if (dataModelIds) {
                inList("dataModel.id", dataModelIds)
            }
        }

        def countItems = clazz.createCriteria().get {
            and {
                if (dataModelIds) {
                    dataModelQuery.delegate = delegate
                    dataModelQuery()
                }
                searchQuery.delegate = delegate
                searchQuery()
                if (query?.statusList) {
                    statusQuery.delegate = delegate
                    statusQuery()
                }
            }
            projections {
                countDistinct('id')
            }
        }

        Date start = new Date()
        def items = clazz.createCriteria().list(params) {
            and {
                if (dataModelIds) {
                    dataModelQuery.delegate = delegate
                    dataModelQuery()
                }
                searchQuery.delegate = delegate
                searchQuery()
                if (query?.statusList) {
                    statusQuery.delegate = delegate
                    statusQuery()
                }
            }
            if ( sortQuery ) {
                order(sortQuery.sort, sortQuery.order)
            }
            projections {
                distinct('id', 'id')
                property('name', 'name')
                property('lastUpdated', 'lastUpdated')
                property('status', 'status')
                property('modelCatalogueId', 'modelCatalogueId')
                if(dataModelIds) {
                    property('dataModel.id', 'dataModel.id')
                    property('dataModel.name', 'dataModel.name')
                }

            }
        }
        Date stop = new Date()
        TimeDuration td = TimeCategory.minus( stop, start )
        log.info 'Query took: {}', td.toString()

        new PagedResultList(items: items, total: countItems as int)
    }

    @CompileDynamic
    def runCriteria(Class clazz, List<Closure> criterias, Map paginateParams) {
        clazz.createCriteria().list(paginateParams) {
            for (Closure criteria in criterias) {
                criteria.delegate = delegate
                criteria()
            }
        }
    }


    boolean shouldSearchProperty(String propertyName, List<SearchCatalogueElementScope> searchCatalogueElementScopeList) {
        List<String> allowedProperties = [
                'name',
                'description',
                'modelCatalogueId',
                'extensions.name',
                'extensions.extensionValue'
        ]
        if ( allowedProperties.contains(propertyName) ) {
            if ( searchCatalogueElementScopeList.contains(SearchCatalogueElementScope.ALL) ) {
                return true
            }
            if ( propertyName == 'name' && searchCatalogueElementScopeList.contains(SearchCatalogueElementScope.NAME) ) {
                return true

            } else if ( propertyName == 'description' && searchCatalogueElementScopeList.contains(SearchCatalogueElementScope.DESCRIPTION) ) {
                return true

            } else if ( propertyName == 'modelCatalogueId' && searchCatalogueElementScopeList.contains(SearchCatalogueElementScope.MODELCATALOGUEID) ) {
                return true

            } else if ( propertyName == 'extensions.name' && searchCatalogueElementScopeList.contains(SearchCatalogueElementScope.EXTENSIONNAME) ) {
                return true

            } else if ( propertyName == 'extensions.extensionValue' && searchCatalogueElementScopeList.contains(SearchCatalogueElementScope.EXTENSIONVALUE) ) {
                return true
            }
        }
        return false
    }

    List<ElementStatus> findAllElementStatus() {
        [ElementStatus.FINALIZED, ElementStatus.DRAFT, ElementStatus.PENDING]
    }

    @CompileDynamic
    @Transactional(readOnly = true)
    List<IdName> findAllDataModel() {
        List<ElementStatus> statusList = findAllElementStatus()
        SearchQuery searchStatusQuery = new SearchQuery(statusList: statusList,
                search: null,
                metadataDomain: MetadataDomain.DATA_MODEL)

        dataModelGormService.findAllBySearchStatusQuery(searchStatusQuery, null, []).sort { DataModel a, DataModel b ->
            a.name <=> b.name
        }collect { DataModel dataModel ->
            new IdName(id: dataModel.id,
                    name: nameForDataModel(dataModel).toString())
        }
    }

    String nameForDataModel(DataModel dataModel) {
        final String dataModelName = dataModel.name ? (dataModel.name.length() > 30 ? dataModel.name.substring(0,29) + '...' : dataModel.name ) : ''
        "${dataModelName} ${dataModel.semanticVersion} (${dataModel.status})".toString()
    }

    CatalogueElementSearchResult searchResult(Class clazz, MetadataDomain domain, SearchQuery searchQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        PagedResultList results = resultsOfBuildableCriteriaBySearchStatusQuery(clazz, searchQuery, sortQuery, paginationQuery)
        List<CatalogueElementViewModel> catalogueElementList = CatalogueElementViewModelUtils.ofProjections(domain, results.items)
        new CatalogueElementSearchResult(total: results.getTotalCount(), viewModels: catalogueElementList)
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllBusinessRuleViewBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        searchResult(ValidationRule.class, MetadataDomain.BUSINESS_RULE, searchStatusQuery, sortQuery, paginationQuery)
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllMeasurementUnitViewBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        searchResult(ValidationRule.class, MetadataDomain.MEASUREMENT_UNIT, searchStatusQuery, sortQuery, paginationQuery)
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllDataTypeViewBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        searchResult(DataType.class, MetadataDomain.DATA_TYPE, searchStatusQuery, sortQuery, paginationQuery)
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllEnumeratedTypeViewBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        searchResult(EnumeratedType.class, MetadataDomain.ENUMERATED_TYPE, searchStatusQuery, sortQuery, paginationQuery)
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllDataClassViewBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        searchResult(DataClass.class, MetadataDomain.DATA_CLASS, searchStatusQuery, sortQuery, paginationQuery)
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllCatalogueElementViewBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        searchResult(CatalogueElement.class, MetadataDomain.CATALOGUE_ELEMENT, searchStatusQuery, sortQuery, paginationQuery)
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllDataElementViewBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        searchResult(DataElement.class, MetadataDomain.DATA_ELEMENT, searchStatusQuery, sortQuery, paginationQuery)
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllTagViewBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        searchResult(Tag.class, MetadataDomain.TAG, searchStatusQuery, sortQuery, paginationQuery)
    }


    @CompileDynamic
    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllDataModelViewBySearchStatusQuery(SearchQuery searchQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        CatalogueElementSearchResult searchResult =
                searchResult(DataModel.class, MetadataDomain.DATA_MODEL, searchQuery, sortQuery, paginationQuery)
        List<Long> dataModelIds = searchResult.viewModels*.id
        List<Long> allowedDataModelIds = dataModelGormService.findAll()*.id
        dataModelIds = dataModelIds.intersect(allowedDataModelIds)
        Map<Long, List<AssetViewModel>> dataModelToAssets = findAllAssetViewModelByPublishedStatus(dataModelIds, [PublishedStatus.PUBLISHED])
        int total = countAllDataModelBySearchStatusQuery(searchQuery)
        List<DataModelViewModel> viewModels = searchResult.viewModels.findAll { dataModelIds.contains(it.id) }
                .collect {
            new DataModelViewModel(
                    id: it.id,
                    name: it.name,
                    lastUpdated: it.lastUpdated,
                    status: it.status,
                    assetsList: dataModelToAssets.get(it.id)
            )
        }
        new CatalogueElementSearchResult(total: total, viewModels: viewModels)
    }

    Map<Long, List<AssetViewModel>>  findAllAssetViewModelByPublishedStatus(List<Long> dataModelIds, List<PublishedStatus> statusList = []) {
        Map<Long, List<AssetViewModel>> m = [:]
        for ( Long dataModelId : dataModelIds ) {
            m[dataModelId] = findAllAssetViewModelByDataModelIdAndPublishedStatus(dataModelId, statusList)
        }
        m
    }

    @CompileDynamic
    List<AssetViewModel> findAllAssetViewModelByDataModelIdAndPublishedStatus(Long dataModelId, List<PublishedStatus> statusList) {
        DetachedCriteria<Asset> query = assetGormService.findQueryByDataModelIdAndPublishedStatusList(dataModelId, statusList)
        def results = query.list() {
            projections {
                property('id')
                property('name')
                property('publishedStatus')
            }
        }
        findAllAssetViewModelByProjections(results)
    }

    @CompileDynamic
    List<AssetViewModel> findAllAssetViewModelByProjections(def results) {
        results.collect { def arr ->
            new AssetViewModel(id: arr[0] as Long, name: arr[1] as String)
        }
    }

    int countAllBySearchStatusQuery(Long dataModelId, MetadataDomain metadataDomain, SearchQuery searchStatusQuery) {
        if ( metadataDomain == MetadataDomain.DATA_MODEL ) {
            return countAllDataModelBySearchStatusQuery(searchStatusQuery)

        } else if ( metadataDomain == MetadataDomain.DATA_ELEMENT ) {
            return dataElementGormService.countByDataModelAndSearchStatusQuery(dataModelId, searchStatusQuery) as int

        } else if ( metadataDomain == MetadataDomain.DATA_CLASS ) {
            return dataClassGormService.countByDataModelAndSearchStatusQuery(dataModelId, searchStatusQuery) as int

        } else if ( metadataDomain == MetadataDomain.ENUMERATED_TYPE ) {
            return enumeratedTypeGormService.countByDataModelAndSearchStatusQuery(dataModelId, searchStatusQuery) as int

        } else if ( metadataDomain == MetadataDomain.DATA_TYPE ) {
            return dataTypeGormService.countByDataModelAndSearchStatusQuery(dataModelId, searchStatusQuery) as int

        } else if ( metadataDomain == MetadataDomain.MEASUREMENT_UNIT ) {
            return measurementUnitGormService.countByDataModelAndSearchStatusQuery(dataModelId, searchStatusQuery) as int

        } else if ( metadataDomain == MetadataDomain.BUSINESS_RULE ) {
            return validationRuleGormService.countByDataModelAndSearchStatusQuery(dataModelId, searchStatusQuery) as int

        } else if ( metadataDomain == MetadataDomain.TAG ) {
            return tagGormService.countByDataModelAndSearchStatusQuery(dataModelId, searchStatusQuery) as int
        }

        0
    }

    @Transactional(readOnly = true)
    int countAllDataModelBySearchStatusQuery(SearchQuery searchStatusQuery) {
        (dataModelGormService.findAllBySearchStatusQuery(searchStatusQuery, null, []) ?: [] ).size()
    }

    List<MetadataDomain> metadataDomainList() {
        [
                MetadataDomain.CATALOGUE_ELEMENT,
                MetadataDomain.DATA_MODEL,
                MetadataDomain.DATA_ELEMENT,
                MetadataDomain.DATA_CLASS,
                MetadataDomain.ENUMERATED_TYPE,
                MetadataDomain.DATA_TYPE,
                MetadataDomain.MEASUREMENT_UNIT,
                MetadataDomain.BUSINESS_RULE,
                MetadataDomain.TAG
        ]
    }
}




@CompileStatic
class CatalogueElementSearchResult {
    int total
    List<CatalogueElementViewModel> viewModels
}