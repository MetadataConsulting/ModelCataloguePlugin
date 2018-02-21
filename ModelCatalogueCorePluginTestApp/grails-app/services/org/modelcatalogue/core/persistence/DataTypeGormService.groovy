package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.springframework.context.MessageSource

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
}
