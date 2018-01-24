package org.modelcatalogue.core.persistence

import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.springframework.context.MessageSource
import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.DataElement

class DataElementGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    DataElement findById(long id) {
        DataElement.get(id)
    }

    @Transactional(readOnly = true)
    DataElement findByName(String name) {
        findQueryByName(name).get()
    }

    @Transactional
    DataElement saveByNameAndPrimitiveType(String name, DataType dataType) {
        save(new DataElement(name: name, dataType: dataType))
    }

    @Transactional
    DataElement save(DataElement dataElementInstance) {
        if ( !dataElementInstance.save() ) {
            warnErrors(dataElementInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        dataElementInstance
    }

    @Transactional
    DataElement saveWithNameAndDescription(String name, String description) {
        save(new DataElement(name: name, description: description))
    }

    @Transactional
    DataElement saveWithNameAndDescriptionAndDataType(String name, String description, DataType dataType) {
        save(new DataElement(name: name, description: description, dataType: dataType))
    }

    @Transactional
    DataElement saveWithNameAndDescriptionAndStatus(String name, String description, ElementStatus status) {
        save(new DataElement(name: name, description: description, status: status))
    }

    @Transactional
    DataElement saveWithNameAndDescriptionAndStatusAndDataType(String name, String description, ElementStatus status, DataType dataType) {
        save(new DataElement(name: name, description: description, status: status, dataType: dataType))
    }

    @Transactional
    DataElement saveWithNameAndDescriptionAndStatusAndDataModel(String name, String description, ElementStatus status, DataModel dataModel) {
        save(new DataElement(name: name, description: description, status: status, dataModel: dataModel))
    }

    protected DetachedCriteria<DataElement> findQueryByName(String nameParam) {
        DataElement.where { name == nameParam }
    }

    @Transactional(readOnly = true)
    List<DataElement> findAllByDataModel(DataModel dataModel) {
        findQueryByDataModel(dataModel).list()
    }

    protected DetachedCriteria<DataElement> findQueryByDataModel(DataModel dataModelParam) {
        DataElement.where { dataModel == dataModelParam }
    }

    @Transactional(readOnly = true)
    List<DataElement> findAllByDataType(DataType dataType) {
        findQueryByDataType(dataType).list()
    }

    @Transactional(readOnly = true)
    Number countByDataType(DataType dataType) {
        findQueryByDataType(dataType).count()
    }

    protected DetachedCriteria<DataElement> findQueryByDataType(DataType dataTypeParam) {
        DataElement.where {
            dataType == dataTypeParam
        }
    }

    @Transactional(readOnly = true)
    List<DataElement> findAllByDataTypeAndStatusInList(DataType dataType, List<ElementStatus> elementStatuses) {
        findQueryByDataTypeAndStatusInList(dataType, elementStatuses).list()
    }

    @Transactional(readOnly = true)
    Number countByDataTypeAndStatusInList(DataType dataType, List<ElementStatus> elementStatuses) {
        findQueryByDataTypeAndStatusInList(dataType, elementStatuses).count()
    }

    protected DetachedCriteria<DataElement> findQueryByDataTypeAndStatusInList(DataType dataTypeParam, List<ElementStatus> elementStatuses) {
        DataElement.where {
            dataType == dataTypeParam && status in elementStatuses
        }
    }
}
