package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.springframework.context.MessageSource

class DataClassGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    DataClass findById(long id) {
        DataClass.get(id)
    }

    @Transactional
    DataClass saveWithNameAndDescription(String name, String description) {
        save(new DataClass(name: name, description: description))
    }

    @Transactional
    DataClass save(DataClass dataClassInstance) {
        if ( !dataClassInstance.save() ) {
            warnErrors(dataClassInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        dataClassInstance
    }

    @Transactional
    DataClass saveWithNameAndDescriptionAndStatus(String name, String description, ElementStatus status) {
        save(new DataClass(name: name, description: description, status: status))
    }

    @Transactional(readOnly = true)
    DataClass findByModelCatalogueIdAndDataModel(String modelCatalogueId, DataModel dataModel) {
        findQueryByModelCatalogueIdAndDataModel(modelCatalogueId, dataModel).get()
    }

    protected DetachedCriteria<DataClass> findQueryByModelCatalogueIdAndDataModel(String modelCatalogueIdParam, DataModel dataModelParam) {
        DataClass.where { modelCatalogueId == modelCatalogueIdParam && dataModel == dataModelParam }
    }

    @Transactional(readOnly = true)
    DataClass findByNameAndDataModel(String name, DataModel dataModel) {
        List<DataClass> dataClassList = findQueryByNameAndDataModel(name, dataModel).max(1).list()
        dataClassList.getAt(0)
    }

    protected DetachedCriteria<DataClass> findQueryByNameAndDataModel(String nameParam, DataModel dataModelParam) {
        DataClass.where { name == nameParam && dataModel == dataModelParam }
    }
}
