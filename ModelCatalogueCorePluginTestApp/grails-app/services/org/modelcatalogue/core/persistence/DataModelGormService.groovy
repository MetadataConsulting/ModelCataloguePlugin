package org.modelcatalogue.core.persistence

import org.modelcatalogue.core.DataModel
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional

class DataModelGormService {

    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERVISOR')")
    List<DataModel> findAll() {
        DataModel.findAll()
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
}
