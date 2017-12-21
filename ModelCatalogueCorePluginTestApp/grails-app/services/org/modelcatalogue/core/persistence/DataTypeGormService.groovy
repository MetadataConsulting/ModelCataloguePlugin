package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
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
    DataType findByModelCatalogueIdAndDataModel(String modelCatalogueId, DataModel dataModel) {
        findQueryByModelCatalogueIdAndDataModel(modelCatalogueId, dataModel).get()
    }

    protected DetachedCriteria<DataType> findQueryByModelCatalogueIdAndDataModel(String modelCatalogueIdParam, DataModel dataModelParam) {
        DataType.where { modelCatalogueId == modelCatalogueIdParam && dataModel == dataModelParam }
    }

    @Transactional(readOnly = true)
    DataType findByNameAndDataModel(String name, DataModel dataModel) {
        findQueryByNameAndDataModel(name, dataModel).get()
    }

    protected DetachedCriteria<DataType> findQueryByNameAndDataModel(String nameParam, DataModel dataModelParam) {
        DataType.where { name == nameParam && dataModel == dataModelParam }
    }
}
