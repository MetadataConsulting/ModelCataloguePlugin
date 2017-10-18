package org.modelcatalogue.core

import org.modelcatalogue.core.util.DataModelFilter
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import groovy.transform.CompileStatic
import com.google.common.collect.ImmutableSet

@CompileStatic
class DataModelGormService {

    static transactional = true

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
    @Transactional(readOnly = true)
    DataModel read(long id) {
        DataModel.get(id)
    }

    @PreAuthorize("hasPermission(#id, 'org.modelcatalogue.core.DataModel', admin)  or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERVISOR')")
    @Transactional(readOnly = true)
    DataModel get(long id) {
        DataModel.get(id)
    }

    boolean hasAccessToEveryDataModelInFilterIncludes(DataModelFilter dataModelFilter) {
        ImmutableSet<Long> dataModelIds = dataModelFilter.includes
        List<Long> dataModelIdList = dataModelIds.toList()
        List<DataModel> dataModelList = findAllInIdList(dataModelIdList)
        ( dataModelList.size() == dataModelIdList.size() )
    }

}
