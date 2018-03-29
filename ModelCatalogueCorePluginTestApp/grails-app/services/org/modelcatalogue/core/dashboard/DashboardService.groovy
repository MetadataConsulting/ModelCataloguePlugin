package org.modelcatalogue.core.dashboard

import grails.gorm.DetachedCriteria
import grails.orm.PagedResultList
import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.grails.datastore.mapping.query.api.BuildableCriteria
import org.grails.datastore.mapping.transactions.Transaction
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Tag
import org.modelcatalogue.core.ValidationRule
import org.modelcatalogue.core.persistence.AssetGormService
import org.modelcatalogue.core.persistence.DataClassGormService
import org.modelcatalogue.core.persistence.DataElementGormService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.persistence.DataTypeGormService
import org.modelcatalogue.core.persistence.EnumeratedTypeGormService
import org.modelcatalogue.core.persistence.MeasurementUnitGormService
import org.modelcatalogue.core.persistence.TagGormService
import org.modelcatalogue.core.persistence.ValidationRuleGormService
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
import org.springframework.security.access.prepost.PostFilter

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

    CatalogueElementSearchResult findAllBySearchStatusQuery(Long dataModelId, MetadataDomain metadataDomain, SearchStatusQuery searchStatusQuery = null, SortQuery sortQuery = null, PaginationQuery paginationQuery = null) {
        if ( metadataDomain == MetadataDomain.DATA_MODEL ) {
            return findAllDataModelViewBySearchStatusQuery(searchStatusQuery, sortQuery, paginationQuery)

        } else if ( metadataDomain == MetadataDomain.DATA_ELEMENT ) {
            return findAllDataElementViewBySearchStatusQuery(dataModelId, searchStatusQuery, sortQuery, paginationQuery)

        } else if ( metadataDomain == MetadataDomain.DATA_CLASS ) {
            return findAllDataClassViewBySearchStatusQuery(dataModelId, searchStatusQuery, sortQuery, paginationQuery)

        } else if ( metadataDomain == MetadataDomain.ENUMERATED_TYPE ) {
            return findAllEnumeratedTypeViewBySearchStatusQuery(dataModelId, searchStatusQuery, sortQuery, paginationQuery)

        } else if ( metadataDomain == MetadataDomain.DATA_TYPE ) {
            return findAllDataTypeViewBySearchStatusQuery(dataModelId, searchStatusQuery, sortQuery, paginationQuery)

        } else if ( metadataDomain == MetadataDomain.MEASUREMENT_UNIT ) {
            return findAllMeasurementUnitViewBySearchStatusQuery(dataModelId, searchStatusQuery, sortQuery, paginationQuery)

        } else if ( metadataDomain == MetadataDomain.BUSINESS_RULE) {
            return findAllBusinessRuleViewBySearchStatusQuery(dataModelId, searchStatusQuery, sortQuery, paginationQuery)

        } else if ( metadataDomain == MetadataDomain.TAG) {
            return findAllTagViewBySearchStatusQuery(dataModelId, searchStatusQuery, sortQuery, paginationQuery)
        }
        new CatalogueElementSearchResult(total: 0, viewModels: [])
    }

    @CompileDynamic
    PagedResultList resultsOfBuildableCriteriaBySearchStatusQuery(BuildableCriteria criteria, Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        def items = criteria.list(max: paginationQuery?.max, offset: paginationQuery?.offset ?: 0) {
            createAlias('dataModel', 'dataModel')
            and {
                if (dataModelId) {
                    eq("dataModel.id", dataModelId)
                }
                if ( searchStatusQuery ) {
                    if (searchStatusQuery.search) {
                        if (searchStatusQuery.searchWithWhitespace) {
                            ilike("name", "% ${searchStatusQuery.search} %")
                        }
                        else {
                            ilike("name", "%${searchStatusQuery.search}%")
                        }
                    }
                    if (searchStatusQuery.statusList) {
                        inList("status", searchStatusQuery.statusList)
                    }
                }
            }
            if ( paginationQuery ) {
                maxResults(paginationQuery.max)
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
    CatalogueElementSearchResult findAllBusinessRuleViewBySearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = ValidationRule.createCriteria()
        PagedResultList results = resultsOfBuildableCriteriaBySearchStatusQuery(c, dataModelId, searchStatusQuery, sortQuery, paginationQuery)
        new CatalogueElementSearchResult(total: results.getTotalCount(), viewModels: CatalogueElementViewModelUtils.ofProjections(MetadataDomain.BUSINESS_RULE, results))
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllMeasurementUnitViewBySearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = MeasurementUnit.createCriteria()
        PagedResultList results = resultsOfBuildableCriteriaBySearchStatusQuery(c, dataModelId, searchStatusQuery, sortQuery, paginationQuery)
        new CatalogueElementSearchResult(total: results.getTotalCount(), viewModels: CatalogueElementViewModelUtils.ofProjections(MetadataDomain.MEASUREMENT_UNIT, results))
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllDataTypeViewBySearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = DataType.createCriteria()
        PagedResultList results = resultsOfBuildableCriteriaBySearchStatusQuery(c, dataModelId, searchStatusQuery, sortQuery, paginationQuery)
        new CatalogueElementSearchResult(total: results.getTotalCount(), viewModels: CatalogueElementViewModelUtils.ofProjections(MetadataDomain.DATA_TYPE, results))
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllEnumeratedTypeViewBySearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = EnumeratedType.createCriteria()
        PagedResultList results = resultsOfBuildableCriteriaBySearchStatusQuery(c, dataModelId, searchStatusQuery, sortQuery, paginationQuery)
        new CatalogueElementSearchResult(total: results.getTotalCount(), viewModels: CatalogueElementViewModelUtils.ofProjections(MetadataDomain.ENUMERATED_TYPE, results))
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllDataClassViewBySearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = DataClass.createCriteria()
        PagedResultList results = resultsOfBuildableCriteriaBySearchStatusQuery(c, dataModelId, searchStatusQuery, sortQuery, paginationQuery)
        new CatalogueElementSearchResult(total: results.getTotalCount(), viewModels: CatalogueElementViewModelUtils.ofProjections(MetadataDomain.DATA_CLASS, results))
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllDataElementViewBySearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = DataElement.createCriteria()
        PagedResultList results = resultsOfBuildableCriteriaBySearchStatusQuery(c, dataModelId, searchStatusQuery, sortQuery, paginationQuery)
        new CatalogueElementSearchResult(total: results.getTotalCount(), viewModels: CatalogueElementViewModelUtils.ofProjections(MetadataDomain.DATA_ELEMENT, results))
    }

    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllTagViewBySearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = Tag.createCriteria()
        PagedResultList results = resultsOfBuildableCriteriaBySearchStatusQuery(c, dataModelId, searchStatusQuery, sortQuery, paginationQuery)
        new CatalogueElementSearchResult(total: results.getTotalCount(), viewModels: CatalogueElementViewModelUtils.ofProjections(MetadataDomain.TAG, results))
    }

    @CompileDynamic
    @Transactional(readOnly = true)
    CatalogueElementSearchResult findAllDataModelViewBySearchStatusQuery(SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        List<DataModel> dataModelList = dataModelGormService.findAllBySearchStatusQuery(searchStatusQuery, sortQuery, paginationQuery, ['asset'])

        List<Long> dataModelIds = dataModelList*.id as List<Long>
        Map<Long, List<AssetViewModel>> dataModelToAssets = findAllAssetViewModelByPublishedStatus(dataModelIds, [PublishedStatus.PUBLISHED])
        int total = countAllDataModelBySearchStatusQuery(searchStatusQuery)
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

    int countAllBySearchStatusQuery(Long dataModelId, MetadataDomain metadataDomain, SearchStatusQuery searchStatusQuery) {
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
    int countAllDataModelBySearchStatusQuery(SearchStatusQuery searchStatusQuery) {
        (dataModelGormService.findAllBySearchStatusQuery(searchStatusQuery, null, null, []) ?: [] ).size()
    }

    List<MetadataDomain> metadataDomainList() {
        [
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
