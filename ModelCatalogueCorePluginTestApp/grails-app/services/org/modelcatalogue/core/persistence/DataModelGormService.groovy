package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.datamodel.DataModelRow
import org.springframework.context.MessageSource
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize

class DataModelGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAllByCriteriaAndParams(DetachedCriteria<DataModel> criteria, Map<String, Object> params) {
        criteria.list(params)
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
