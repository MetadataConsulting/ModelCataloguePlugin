package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dashboard.SearchQuery
import org.slf4j.Logger
import org.springframework.context.MessageSource

@Slf4j
class DataTypeGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    DataType findById(Long id) {
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
    Number countByDataModelAndSearchStatusQuery(Long dataModelId, SearchQuery searchStatusQuery) {
        findQueryByDataModelAndSearchStatusQuery(dataModelId, searchStatusQuery).count()
    }

    @CompileStatic
    DetachedCriteria<DataType> findQueryByDataModelAndSearchStatusQuery(Long dataModelId, SearchQuery searchStatusQuery) {
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

    DetachedCriteria<DataType> queryByIds(List<Long> ids) {
        DataType.where { id in ids }
    }

    @Transactional(readOnly = true)
    List<DataType> findAllByIds(List<Long> ids) {
        if ( !ids ) {
            return [] as List<DataType>
        }
        queryByIds(ids).list()
    }

    @Override
    Logger getLog() {
        return log
    }
}
