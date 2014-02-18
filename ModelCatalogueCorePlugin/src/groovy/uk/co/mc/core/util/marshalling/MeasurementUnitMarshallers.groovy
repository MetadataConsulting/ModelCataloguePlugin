package uk.co.mc.core.util.marshalling

import grails.converters.XML
import uk.co.mc.core.MeasurementUnit

/**
 * Marshallers for MeasurementUnit.
 */
class MeasurementUnitMarshallers extends CatalogueElementMarshallers {

    MeasurementUnitMarshallers() {
        super(MeasurementUnit)
    }

    protected Map<String, Object> prepareJsonMap(unit) {
        if (!unit) return [:]
        def ret = super.prepareJsonMap(unit)
        ret.putAll(symbol: unit.symbol)
        return ret
    }

    protected void buildXml(el, XML xml) {
        super.buildXml(el, xml)
        xml.build {
            symbol el.symbol
        }
    }
}
