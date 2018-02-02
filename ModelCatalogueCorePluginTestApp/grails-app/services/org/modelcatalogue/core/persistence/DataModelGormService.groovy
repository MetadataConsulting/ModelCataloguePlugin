package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dashboard.SearchStatusQuery
import org.modelcatalogue.core.datamodel.DataModelRow
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.SortQuery
import org.springframework.context.MessageSource
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize

class DataModelGormService implements WarnGormErrors {

    MessageSource messageSource

    @CompileStatic
    DetachedCriteria<DataModel> findQueryBySearchStatusQuery(SearchStatusQuery searchStatusQuery) {
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

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAllBySearchStatusQuery(SearchStatusQuery searchStatusQuery, SortQuery sortQuery, PaginationQuery paginationQuery) {
        DetachedCriteria<DataModel> query = findQueryBySearchStatusQuery(searchStatusQuery)
        if ( sortQuery.sort != null && sortQuery.order != null) {
            query = query.sort(sortQuery.sort, sortQuery.order)
        }
        if ( paginationQuery.max && paginationQuery.offset ) {
            return query.list(max: paginationQuery.max, offset: paginationQuery.offset)
        }
        query.list()
    }

    @Transactional(readOnly = true)
    Number countAllBySearchStatusQuery(SearchStatusQuery searchStatusQuery) {
        DetachedCriteria<DataModel> query = findQueryBySearchStatusQuery(searchStatusQuery)
        query.projections {
            property('id')
        }.count()
    }

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAllByCriteria(DetachedCriteria<DataModel> criteria) {
        criteria.list()
    }

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAllByNameNotEqual(String nameParam) {
        DataModel.where { name != nameParam }.list()
    }

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAll() {
        DataModel.findAll()
    }

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAllNotInStatus(ElementStatus statusParam) {
        DataModel.where {
            status != statusParam
        }.sort('name', 'asc').list()
    }

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAllInIdList(List<Long> dataModelIdList) {
        DataModel.where { id in dataModelIdList }.list()
    }

    @PreAuthorize("hasPermission(#id, 'org.modelcatalogue.core.DataModel', read) or hasPermission(#id, 'org.modelcatalogue.core.DataModel', admin) or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERVISOR')")
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

    @PreAuthorize("hasRole('ROLE_METADATA_CURATOR') or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERVISOR')")
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
