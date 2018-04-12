package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dashboard.SearchQuery
import org.modelcatalogue.core.datamodel.DataModelRow
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.SortQuery
import org.springframework.context.MessageSource
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize

class DataModelGormService implements WarnGormErrors {

    MessageSource messageSource

    @CompileStatic
    DetachedCriteria<DataModel> findQueryBySearchStatusQuery(SearchQuery searchStatusQuery) {
        DetachedCriteria<DataModel> query = DataModel.where {}
        if ( searchStatusQuery.statusList ) {
            query = query.where { status in searchStatusQuery.statusList }
        }
        if ( searchStatusQuery.search ) {
            String term = "%${searchStatusQuery.search}%".toString()
            query = query.where { name =~ term }
        }
        query
    }

    @CompileStatic
    DetachedCriteria<DataModel> findQueryBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery) {
        DetachedCriteria<DataModel> query = findQueryBySearchStatusQuery(searchStatusQuery)
        if ( sortQuery?.sort != null && sortQuery?.order != null) {
            query = query.sort(sortQuery.sort, sortQuery.order)
        }
        query
    }

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAllBySearchStatusQuery(SearchQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery, List<String> joinProperties) {
        DetachedCriteria<DataModel> query = findQueryBySearchStatusQuery(searchStatusQuery, sortQuery)
        if ( joinProperties ) {
            for ( String propertyName : joinProperties ) {
                query.join(propertyName)
            }
        }
        Map m = paginationQuery?.toMap() ?: Collections.emptyMap()
        query.list(m)
    }

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAllByCriteria(DetachedCriteria<DataModel> criteria) {
        criteria.list()
    }

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAllByNameNotEqual(String nameParam) {
        DataModel.where { name != nameParam }.list()
    }

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAll() {
        DataModel.findAll()
    }

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAllNotInStatus(ElementStatus statusParam) {
        DataModel.where {
            status != statusParam
        }.sort('name', 'asc').list()
    }

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAllInStatus(ElementStatus statusParam) {
        DataModel.where {
            status == statusParam
        }.sort('name', 'asc').list()
    }

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAllInIdList(List<Long> dataModelIdList) {
        DataModel.where { id in dataModelIdList }.list()
    }

    @PreAuthorize("hasPermission(#id, 'org.modelcatalogue.core.DataModel', read) or hasPermission(#id, 'org.modelcatalogue.core.DataModel', admin) or hasRole('ROLE_SUPERVISOR')")
    @Transactional(readOnly = true)
    DataModel findById(long id) {
        DataModel.get(id)
    }

    List<DataModelRow> findAllDataModelRows() {
        findAll().collect { DataModel dataModel ->
            new DataModelRow(id: dataModel.id,
                    name: dataModel.name,
                    status: dataModel.status,
                    semanticVersion: dataModel.semanticVersion)
        }
    }

    @PreAuthorize("hasRole('ROLE_METADATA_CURATOR') or hasRole('ROLE_SUPERVISOR')")
    @Transactional
    DataModel saveWithNameAndDescriptonAndStatus(String name, String description, ElementStatus status) {
        DataModel dataModelInstance = new DataModel(name: name, description: description, status: status)
        if ( !dataModelInstance.save() ) {
            warnErrors(dataModelInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        dataModelInstance
    }
}
