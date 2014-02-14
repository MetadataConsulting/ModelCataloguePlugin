package uk.co.mc.core.util.marshalling

import grails.converters.JSON
import grails.converters.XML
import uk.co.mc.core.MeasurementUnit

/**
 * Marshallers for MeasurementUnit.
 */
class MeasurementUnitMarshallers implements MarshallersProvider {

    void register() {
        JSON.registerObjectMarshaller(MeasurementUnit) { MeasurementUnit unit ->
            def ret = [symbol: unit.symbol]
            ret.putAll(CatalogueElementMarshallers.prepareJsonMap(unit))
            return ret
        }
        XML.registerObjectMarshaller(MeasurementUnit) { MeasurementUnit el, XML xml ->
            CatalogueElementMarshallers.buildXml(el, xml)
            xml.build {
                symbol el.symbol
            }
        }
    }
}
