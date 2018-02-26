package org.modelcatalogue.core.dashboard

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.grails.datastore.mapping.query.api.BuildableCriteria
import org.grails.datastore.mapping.transactions.Transaction
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
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

    List findAllBySearchStatusQuery(Long dataModelId, MetadataDomain metadataDomain, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        if ( metadataDomain == MetadataDomain.DATA_MODEL ) {
            return findAllDataModelViewBySearchStatusQuery(dataModelId, searchStatusQuery, sortQuery, paginationQuery)

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
        []
    }

    @CompileDynamic
    Object resultsOfBuildableCriteriaBySearchStatusQuery(BuildableCriteria criteria, Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        criteria.list {
            createAlias('dataModel', 'dataModel')
            and {
                if (dataModelId) {
                    eq("dataModel.id", dataModelId)
                }
                if (searchStatusQuery.search) {
                    ilike("name", "%${searchStatusQuery.search}%")
                }
                if (searchStatusQuery.statusList) {
                    inList("status", searchStatusQuery.statusList)
                }
            }
            maxResults(paginationQuery.max)
            firstResult(paginationQuery.offset)
            order(sortQuery.sort, sortQuery.order)
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
    List<CatalogueElementViewModel> findAllBusinessRuleViewBySearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = ValidationRule.createCriteria()
        Object results = resultsOfBuildableCriteriaBySearchStatusQuery(c, dataModelId, searchStatusQuery, sortQuery, paginationQuery)
        CatalogueElementViewModelUtils.ofProjections(MetadataDomain.BUSINESS_RULE, results)
    }

    @Transactional(readOnly = true)
    List<CatalogueElementViewModel> findAllMeasurementUnitViewBySearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = MeasurementUnit.createCriteria()
        Object results = resultsOfBuildableCriteriaBySearchStatusQuery(c, dataModelId, searchStatusQuery, sortQuery, paginationQuery)
        CatalogueElementViewModelUtils.ofProjections(MetadataDomain.MEASUREMENT_UNIT, results)
    }

    @Transactional(readOnly = true)
    List<CatalogueElementViewModel> findAllDataTypeViewBySearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = DataType.createCriteria()
        Object results = resultsOfBuildableCriteriaBySearchStatusQuery(c, dataModelId, searchStatusQuery, sortQuery, paginationQuery)
        CatalogueElementViewModelUtils.ofProjections(MetadataDomain.DATA_TYPE, results)
    }

    @Transactional(readOnly = true)
    List<CatalogueElementViewModel> findAllEnumeratedTypeViewBySearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = DataElement.createCriteria()
        Object results = resultsOfBuildableCriteriaBySearchStatusQuery(c, dataModelId, searchStatusQuery, sortQuery, paginationQuery)
        CatalogueElementViewModelUtils.ofProjections(MetadataDomain.ENUMERATED_TYPE, results)
    }

    @Transactional(readOnly = true)
    List<CatalogueElementViewModel> findAllDataClassViewBySearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = DataElement.createCriteria()
        Object results = resultsOfBuildableCriteriaBySearchStatusQuery(c, dataModelId, searchStatusQuery, sortQuery, paginationQuery)
        CatalogueElementViewModelUtils.ofProjections(MetadataDomain.DATA_CLASS, results)
    }

    @Transactional(readOnly = true)
    List<CatalogueElementViewModel> findAllDataElementViewBySearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = DataElement.createCriteria()
        Object results = resultsOfBuildableCriteriaBySearchStatusQuery(c, dataModelId, searchStatusQuery, sortQuery, paginationQuery)
        CatalogueElementViewModelUtils.ofProjections(MetadataDomain.DATA_ELEMENT, results)
    }

    @Transactional(readOnly = true)
    List<CatalogueElementViewModel> findAllTagViewBySearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria c = Tag.createCriteria()
        Object results = resultsOfBuildableCriteriaBySearchStatusQuery(c, dataModelId, searchStatusQuery, sortQuery, paginationQuery)
        CatalogueElementViewModelUtils.ofProjections(MetadataDomain.TAG, results)
    }

    @CompileDynamic
    @Transactional(readOnly = true)
    List<DataModelViewModel> findAllDataModelViewBySearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        DetachedCriteria<DataModel> query = dataModelGormService.findQueryBySearchStatusQuery(searchStatusQuery, sortQuery)
        if ( dataModelId ) {
            query = query.where { id == dataModelId }
        }
        query.join('asset')

        Map m = paginationQuery.toMap()
        def results = query.list(m) {
            projections {
                property('id')
                property('name')
                property('lastUpdated')
                property('status')
                property('semanticVersion')
            }
        }
        Map<Long, List<AssetViewModel>> dataModelToAssets = findAllAssetViewModelByPublishedStatus(results)
        DataModelViewModelUtils.ofProjections(results, dataModelToAssets)
    }

    @CompileDynamic
    List<Long> collectDataModelIds(def results) {
        if ( !results ) {
            return [] as List<Long>
        }
        results.collect { def arr ->
            arr[0] as Long
        }
    }

    Map<Long, List<AssetViewModel>>  findAllAssetViewModelByPublishedStatus(def dataModelResults) {
        List<PublishedStatus> statusList = []
        List<Long> dataModelIds = collectDataModelIds(dataModelResults)
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
        (dataModelGormService.findAllBySearchStatusQuery(searchStatusQuery, null, null) ?: [] ).size()
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
