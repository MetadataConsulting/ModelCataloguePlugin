package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.PrimitiveType

class PrimitiveTypeMarshaller extends DataTypeMarshaller {

    PrimitiveTypeMarshaller() {
        super(PrimitiveType)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = super.prepareJsonMap(el)
        ret.measurementUnit = minimalCatalogueElementJSON(el.measurementUnit)
        if (el.measurementUnit) {
            ret.content = [count: 1, itemType: MeasurementUnit.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/content"]
        } else {
            ret.content = [count: 0, itemType: MeasurementUnit.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/content"]
        }
        ret
    }

}




