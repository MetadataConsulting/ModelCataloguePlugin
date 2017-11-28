package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.datamodel.DataModelRow
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize

class DataModelGormService {

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
    @Transactional
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
}
