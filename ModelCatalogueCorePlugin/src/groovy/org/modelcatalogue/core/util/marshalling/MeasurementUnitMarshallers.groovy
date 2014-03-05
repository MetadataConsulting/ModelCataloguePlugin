package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import org.modelcatalogue.core.MeasurementUnit

/**
 * Marshallers for MeasurementUnit.
 */
class MeasurementUnitMarshallers extends CatalogueElementMarshallers {

    MeasurementUnitMarshallers() {
        super(MeasurementUnit)
    }

    protected Map<String, Object> prepareJsonMap(unit) {
        if (!unit) return [:]
        def ret = Object.prepareJsonMap(unit)
        ret.putAll(symbol: unit.symbol)
        return ret
    }

    protected void buildXml(el, XML xml) {
        Object.buildXml(el, xml)
        xml.build {
            symbol el.symbol
        }
    }
}
