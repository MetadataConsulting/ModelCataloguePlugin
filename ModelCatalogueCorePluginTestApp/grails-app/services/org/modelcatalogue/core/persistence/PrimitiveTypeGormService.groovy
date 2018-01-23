package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.springframework.context.MessageSource

class PrimitiveTypeGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    PrimitiveType save(PrimitiveType primitiveType) {
        if ( !primitiveType.save() ) {
            warnErrors(primitiveType, messageSource)
            transactionStatus.setRollbackOnly()
        }
        primitiveType
    }

    @Transactional
    PrimitiveType saveWithStatusAndNameAndMeasurementUnit(ElementStatus status, String name, MeasurementUnit measurementUnit) {
        PrimitiveType primitiveTypeInstance = new PrimitiveType(status: status, name: name, measurementUnit: measurementUnit)
        save(primitiveTypeInstance)
    }

    @Transactional(readOnly = true)
    PrimitiveType findById(Long id) {
        PrimitiveType.get(id)
    }
}
