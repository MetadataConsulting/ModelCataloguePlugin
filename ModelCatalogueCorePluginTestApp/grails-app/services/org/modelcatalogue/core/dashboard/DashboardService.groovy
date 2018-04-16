package org.modelcatalogue.core.dashboard

import grails.gorm.DetachedCriteria
import grails.orm.PagedResultList
import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.query.api.BuildableCriteria
import org.grails.datastore.mapping.transactions.Transaction
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MaxOffsetSublistUtils
import org.modelcatalogue.core.MeasurementUnit
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
import org.modelcatalogue.core.util.IdName
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.PublishedStatus
import org.modelcatalogue.core.util.SortQuery
import org.modelcatalogue.core.view.AssetViewModel
import org.modelcatalogue.core.view.CatalogueElementViewModelUtils
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

    @Transactional(readOnly = true)
    @CompileDynamic
    List<Long> findDataModelDestinationIdsByDataModelAndRelationshipTypeName(Long dataModelId, RelationshipTypeName typeName) {
        RelationshipType relationshipType = relationshipTypeGormService.findByRelationshipTypeName(typeName)
        if (!relationshipType) {
            log.error('Unable to find {} relationship type', typeName.name)
            return
        }

        DetachedCriteria<Relationship> relationshipQuery = relationshipGormService.findQueryByRelationshipTypeAndSource(relationshipType, CatalogueElement.get(dataModelId))
        relationshipQuery.join('destination')
        relationshipQuery.projections {
            property('destination')
        }
        def results = relationshipQuery.list()

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
            CatalogueElementSearchResult searchResult = new CatalogueElementSearchResult(viewModels: [], total: 0)
            PaginationQuery defaultPaginationQuery = new PaginationQuery(offset: 0)
            for ( CatalogueElementSearchResult result : [
                findAllDataElementViewBySearchStatusQuery(query, sortQuery, defaultPaginationQuery),
                findAllDataClassViewBySearchStatusQuery(query, sortQuery, defaultPaginationQuery),
                findAllEnumeratedTypeViewBySearchStatusQuery(query, sortQuery, defaultPaginationQuery),
                findAllDataTypeViewBySearchStatusQuery(query, sortQuery, defaultPaginationQuery),
                findAllMeasurementUnitViewBySearchStatusQuery(query, sortQuery, defaultPaginationQuery),
                findAllBusinessRuleViewBySearchStatusQuery(query, sortQuery, defaultPaginationQuery),
                findAllTagViewBySearchStatusQuery(query, sortQuery, defaultPaginationQuery),
            ]) {
                searchResult.total += result.total
                searchResult.viewModels.addAll(result.viewModels)
            }

            searchResult.viewModels = MaxOffsetSublistUtils.subList(searchResult.viewModels, paginationQuery.toMap())
            return searchResult

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
    PagedResultList resultsOfBuildableCriteriaBySearchStatusQuery(BuildableCriteria criteria, SearchQuery query, SortQuery sortQuery, PaginationQuery paginationQuery) {
        List<Long> dataModelIds = dataModelIdsBySearchQuery(query)
        Map params = [:]
        if ( paginationQuery ) {
            if ( paginationQuery.max ) {
                params.max = paginationQuery.max
            }
            params.offset = paginationQuery.offset ?: 0
        }
        def items = criteria.list(params) {
            createAlias('dataModel', 'dataModel')
            and {
                if (dataModelIds) {
                    inList("dataModel.id", dataModelIds)
                }
                if ( query ) {
                    if (query.search) {
                        ilike("name", "%${query.search}%")
                    }
                    if (query.statusList) {
                        inList("status", query.statusList)
                    }
                }
            }
            if ( paginationQuery?.max ) {
                maxResults(paginationQuery.max)
            }
            if ( paginationQuery?.offset ) {
                firstResult(paginationQuery.offset)
            }
            if ( sortQuery ) {
                order(sortQuery.sort, sortQuery.order)
            }
            projections {
                property('id', 'id')
                property('name', 'name')
                property('lastUpdated', 'lastUpdated')
                property('status', 'status')
                property('modelCatalogueId', 'modelCatalogueId')
                property('dataModel.id', 'dataModel.id')
                property('dataModel.name', 'dataModel.name')
            }
        }
        return items
    }

    @CompileDynamic
    @Transactional(readOnly = true)
    List<IdName> findAllDataModel() {
        DetachedCriteria<DataModel> query = DataModel.where {}
        query.projections {
            property('id')
            property('name')
            property('semanticVersion')
            property('status')
        }.list().collect { def row ->
            new IdName(id: row[0], name: "${row[1]} ${row[2]} (${row[3]})".toString())
        }
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllBusinessRuleViewBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = ValidationRule.createCriteria()
        PagedResultList results = resultsOfBuildableCriteriaBySearchStatusQuery(c, searchStatusQuery, sortQuery, paginationQuery)
        new CatalogueElementSearchResult(total: results.getTotalCount(), viewModels: CatalogueElementViewModelUtils.ofProjections(MetadataDomain.BUSINESS_RULE, results))
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllMeasurementUnitViewBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = MeasurementUnit.createCriteria()
        PagedResultList results = resultsOfBuildableCriteriaBySearchStatusQuery(c, searchStatusQuery, sortQuery, paginationQuery)
        new CatalogueElementSearchResult(total: results.getTotalCount(), viewModels: CatalogueElementViewModelUtils.ofProjections(MetadataDomain.MEASUREMENT_UNIT, results))
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllDataTypeViewBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = DataType.createCriteria()
        PagedResultList results = resultsOfBuildableCriteriaBySearchStatusQuery(c, searchStatusQuery, sortQuery, paginationQuery)
        new CatalogueElementSearchResult(total: results.getTotalCount(), viewModels: CatalogueElementViewModelUtils.ofProjections(MetadataDomain.DATA_TYPE, results))
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllEnumeratedTypeViewBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = EnumeratedType.createCriteria()
        PagedResultList results = resultsOfBuildableCriteriaBySearchStatusQuery(c, searchStatusQuery, sortQuery, paginationQuery)
        new CatalogueElementSearchResult(total: results.getTotalCount(), viewModels: CatalogueElementViewModelUtils.ofProjections(MetadataDomain.ENUMERATED_TYPE, results))
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllDataClassViewBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = DataClass.createCriteria()
        PagedResultList results = resultsOfBuildableCriteriaBySearchStatusQuery(c, searchStatusQuery, sortQuery, paginationQuery)
        new CatalogueElementSearchResult(total: results.getTotalCount(), viewModels: CatalogueElementViewModelUtils.ofProjections(MetadataDomain.DATA_CLASS, results))
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllDataElementViewBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = DataElement.createCriteria()
        PagedResultList results = resultsOfBuildableCriteriaBySearchStatusQuery(c, searchStatusQuery, sortQuery, paginationQuery)
        new CatalogueElementSearchResult(total: results.getTotalCount(), viewModels: CatalogueElementViewModelUtils.ofProjections(MetadataDomain.DATA_ELEMENT, results))
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllTagViewBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = Tag.createCriteria()
        PagedResultList results = resultsOfBuildableCriteriaBySearchStatusQuery(c, searchStatusQuery, sortQuery, paginationQuery)
        new CatalogueElementSearchResult(total: results.getTotalCount(), viewModels: CatalogueElementViewModelUtils.ofProjections(MetadataDomain.TAG, results))
    }

    @CompileDynamic
    @Transactional(readOnly = true)
    List<Long> findAuthorizedDataModelIds() {
        List<ElementStatus> statusList = [ElementStatus.FINALIZED, ElementStatus.DRAFT, ElementStatus.PENDING]
        SearchQuery searchStatusQuery = new SearchQuery(statusList: statusList, search: null)
        List<DataModel> dataModelList = dataModelGormService.findAllBySearchStatusQuery(searchStatusQuery, null, null, [])
        dataModelList*.id ?: [] as List<Long>
    }

    @CompileDynamic
    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllDataModelViewBySearchStatusQuery(SearchQuery searchQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        List<DataModel> dataModelList = dataModelGormService.findAllBySearchStatusQuery(searchQuery, sortQuery, paginationQuery, ['asset'])

        List<Long> dataModelIds = dataModelList*.id ?: [] as List<Long>
        Map<Long, List<AssetViewModel>> dataModelToAssets = findAllAssetViewModelByPublishedStatus(dataModelIds, [PublishedStatus.PUBLISHED])
        int total = countAllDataModelBySearchStatusQuery(searchQuery)
        List viewModels = DataModelViewModelUtils.ofProjections(dataModelList, dataModelToAssets)
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
        (dataModelGormService.findAllBySearchStatusQuery(searchStatusQuery, null, null, []) ?: [] ).size()
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
    List viewModels
}