package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dashboard.SearchStatusQuery
import org.springframework.context.MessageSource

class DataTypeGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    DataType findById(long id) {
        DataType.get(id)
    }

    @Transactional(readOnly = true)
    DataType findByName(String name) {
        findQueryByName(name).get()
    }

    DetachedCriteria<DataType> findQueryByName(String nameParam) {
        DataType.where { name == nameParam }
    }


    @Transactional
    DataType save(DataType dataTypeInstance) {
        if ( !dataTypeInstance.save() ) {
            warnErrors(dataTypeInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        dataTypeInstance
    }

    @Transactional
    DataType saveWithStatusAndNameAndDescription(ElementStatus status, String name, String description) {
        save(new DataType(name: name, description: description, status: status))
    }

    @Transactional(readOnly = true)
    Number countByDataModelAndSearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery) {
        findQueryByDataModelAndSearchStatusQuery(dataModelId, searchStatusQuery).count()
    }

    @CompileStatic
    DetachedCriteria<DataType> findQueryByDataModelAndSearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery) {
        DetachedCriteria<DataType> query = DataType.where {}
        if ( dataModelId ) {
            query = query.where { dataModel == DataModel.load(dataModelId) }
        }
        if ( searchStatusQuery.statusList ) {
            query = query.where { status in searchStatusQuery.statusList }
        }
        if ( searchStatusQuery.search ) {
            String term = "%${searchStatusQuery.search}%".toString()
            query = query.where { name =~ term }
        }
        query
    }
}
