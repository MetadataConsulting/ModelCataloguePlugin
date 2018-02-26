package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.persistence.MeasurementUnitGormService
import org.modelcatalogue.core.persistence.PrimitiveTypeGormService
import org.springframework.beans.factory.annotation.Autowired

/**
 * Marshallers for MeasurementUnit.
 */
class MeasurementUnitMarshaller extends CatalogueElementMarshaller {

    @Autowired
    PrimitiveTypeGormService primitiveTypeGormService

    MeasurementUnitMarshaller() {
        super(MeasurementUnit)
    }

    protected Map<String, Object> prepareJsonMap(MeasurementUnit unit) {
        if (!unit) {
            return [:]
        }
        Map<String, Object> ret = super.prepareJsonMap(unit)
        ret.putAll(symbol: unit.symbol)
        ret.primitiveTypes = [
                count: unit.isAttached() ? primitiveTypeGormService.countByMeasurementUnit(unit) : 0,
                itemType: PrimitiveType.name,
                link: "/${GrailsNameUtils.getPropertyName(unit.getClass())}/$unit.id/primitiveType"]
        ret
    }
}
