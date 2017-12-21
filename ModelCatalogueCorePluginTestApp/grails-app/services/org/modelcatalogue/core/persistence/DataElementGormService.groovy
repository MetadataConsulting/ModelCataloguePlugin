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
    DataElement findByModelCatalogueIdAndDataModel(String modelCatalogueId, DataModel dataModel) {
        findQueryByModelCatalogueIdAndDataModel(modelCatalogueId, dataModel).get()
    }

    protected DetachedCriteria<DataElement> findQueryByModelCatalogueIdAndDataModel(String modelCatalogueIdParam, DataModel dataModelParam) {
        DataElement.where { modelCatalogueId == modelCatalogueIdParam && dataModel == dataModelParam }
    }

    @Transactional(readOnly = true)
    DataElement findDataElementByNameAndDataModel(String name, DataModel dataModel) {
        findQueryByNameAndDataModel(name, dataModel).get()
    }

    protected DetachedCriteria<DataElement> findQueryByNameAndDataModel(String nameParam, DataModel dataModelParam) {
        DataElement.where { name == nameParam && dataModel == dataModelParam }
    }
}
