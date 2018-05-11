package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
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

    @Transactional(readOnly = true)
    List<PrimitiveType> findAllByMeasurementUnit(MeasurementUnit measurementUnitParam) {
        findQueryByMeasurementUnit(measurementUnitParam).list()
    }

    @Transactional(readOnly = true)
    Number countByMeasurementUnit(MeasurementUnit measurementUnitParam) {
        findQueryByMeasurementUnit(measurementUnitParam).count()
    }

    DetachedCriteria<PrimitiveType> findQueryByMeasurementUnit(MeasurementUnit measurementUnitParam) {
        PrimitiveType.where {
            measurementUnit == measurementUnitParam
        }
    }

    DetachedCriteria<PrimitiveType> queryByIds(List<Long> ids) {
        PrimitiveType.where { id in ids }
    }

    @Transactional(readOnly = true)
    List<PrimitiveType> findAllByIds(List<Long> ids) {
        if ( !ids ) {
            return [] as List<PrimitiveType>
        }
        queryByIds(ids).list()
    }
}
