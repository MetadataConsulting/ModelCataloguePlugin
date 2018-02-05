package org.modelcatalogue.core.dashboard

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.persistence.AssetGormService
import org.modelcatalogue.core.persistence.DataElementGormService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.PublishedStatus
import org.modelcatalogue.core.util.SortQuery
import org.modelcatalogue.core.view.AssetViewModel
import org.modelcatalogue.core.view.DataElementViewModel
import org.modelcatalogue.core.view.DataElementViewModelUtils
import org.modelcatalogue.core.view.DataModelViewModel
import org.modelcatalogue.core.view.DataModelViewModelUtils

@CompileStatic
class DashboardService {

    DataModelGormService dataModelGormService
    DataElementGormService dataElementGormService
    AssetGormService assetGormService


    List findAllBySearchStatusQuery(MetadataDomain metadataDomain, SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        if ( metadataDomain == MetadataDomain.DATA_MODEL ) {
            return findAllDataModelViewBySearchStatusQuery(searchStatusQuery, sortQuery, paginationQuery)
        } else if ( metadataDomain == MetadataDomain.DATA_ELEMENT ) {
            return findAllDataElementViewBySearchStatusQuery(searchStatusQuery, sortQuery, paginationQuery)
        }
        []
    }

    @Transactional(readOnly = true)
    @CompileDynamic
    List<DataElementViewModel> findAllDataElementViewBySearchStatusQuery(SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {

        def c = DataElement.createCriteria()
        def results = c.list {
            and {
                if ( searchStatusQuery.search ) {
                    ilike("name", "%${searchStatusQuery.search}%")
                }
                if ( searchStatusQuery.statusList ) {
                    inList("status", searchStatusQuery.statusList)
                }
            }
            maxResults(paginationQuery.max)
            firstResult(paginationQuery.offset)
            order(sortQuery.sort, sortQuery.order)
            createAlias('dataModel','dataModel')
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
        DataElementViewModelUtils.ofProjections(results)
    }

    @Transactional(readOnly = true)
    @CompileDynamic
    List<DataModelViewModel> findAllDataModelViewBySearchStatusQuery(SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        DetachedCriteria<DataModel> query = dataModelGormService.findQueryBySearchStatusQuery(searchStatusQuery, sortQuery)
        query.join('asset')

        Map m = paginationQuery.toMap()
        def results = query.list(m){
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

    int countAllBySearchStatusQuery(MetadataDomain metadataDomain, SearchStatusQuery searchStatusQuery) {
        if ( metadataDomain == MetadataDomain.DATA_MODEL ) {
            return countAllDataModelBySearchStatusQuery(searchStatusQuery)
        } else if ( metadataDomain == MetadataDomain.DATA_ELEMENT ) {
            return dataElementGormService.countBySearchStatusQuery(searchStatusQuery) as int
        }
        0
    }

    @Transactional(readOnly = true)
    int countAllDataModelBySearchStatusQuery(SearchStatusQuery searchStatusQuery) {
        (dataModelGormService.findAllBySearchStatusQuery(searchStatusQuery, null, null) ?: [] ).size()
    }
}
