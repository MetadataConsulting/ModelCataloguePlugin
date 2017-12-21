package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.springframework.context.MessageSource

class MeasurementUnitGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    MeasurementUnit findById(long id) {
        MeasurementUnit.get(id)
    }
    @Transactional(readOnly = true)
    MeasurementUnit findByName(String name) {
        findQueryByName(name).get()
    }

    DetachedCriteria<MeasurementUnit> findQueryByName(String nameParam) {
        MeasurementUnit.where { name == nameParam }
    }

    @Transactional
    MeasurementUnit save(MeasurementUnit measurementUnit) {
        if (!measurementUnit.save()) {
            warnErrors(measurementUnit, messageSource)
            transactionStatus.setRollbackOnly()
        }
        measurementUnit
    }

    @Transactional
    MeasurementUnit saveWithStatusAndSymbolAndNameAndDescription(ElementStatus status, String symbol, String name, String description) {
        save(new MeasurementUnit(status: status, symbol: symbol, name: name, description: description))
    }

    @Transactional
    MeasurementUnit saveWithStatusAndSymbolAndName(ElementStatus status, String symbol, String name) {
        save(new MeasurementUnit(status: status, symbol: symbol, name: name))
    }

    @Transactional(readOnly = true)
    MeasurementUnit findByModelCatalogueIdAndDataModel(String modelCatalogueId, DataModel dataModel) {
        findQueryByModelCatalogueIdAndDataModel(modelCatalogueId, dataModel).get()
    }

    protected DetachedCriteria<MeasurementUnit> findQueryByModelCatalogueIdAndDataModel(String modelCatalogueIdParam, DataModel dataModelParam) {
        MeasurementUnit.where { modelCatalogueId == modelCatalogueIdParam && dataModel == dataModelParam }
    }

    @Transactional(readOnly = true)
    MeasurementUnit findByNameAndDataModel(String name, DataModel dataModel) {
        findQueryByNameAndDataModel(name, dataModel).get()
    }

    protected DetachedCriteria<MeasurementUnit> findQueryByNameAndDataModel(String nameParam, DataModel dataModelParam) {
        MeasurementUnit.where { name == nameParam && dataModel == dataModelParam }
    }

    @Transactional(readOnly = true)
    MeasurementUnit findBySymbolAndDataModel(String muSymbol, DataModel dataModel) {
        findQueryBySymbolAndDataModel(muSymbol, dataModel).get()
    }

    protected DetachedCriteria<MeasurementUnit> findQueryBySymbolAndDataModel(String muSymbol, DataModel dataModelParam) {
        MeasurementUnit.where { symbol == muSymbol && dataModel == dataModelParam }
    }
}
