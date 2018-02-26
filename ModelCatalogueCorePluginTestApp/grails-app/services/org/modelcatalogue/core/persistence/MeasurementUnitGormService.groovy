package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileStatic
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dashboard.SearchStatusQuery
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
    Number countBySearchStatusQuery(SearchStatusQuery searchStatusQuery) {
        findQueryBySearchStatusQuery(searchStatusQuery).count()
    }

    @CompileStatic
    DetachedCriteria<MeasurementUnit> findQueryBySearchStatusQuery(SearchStatusQuery searchStatusQuery) {
        DetachedCriteria<MeasurementUnit> query = MeasurementUnit.where {}
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
