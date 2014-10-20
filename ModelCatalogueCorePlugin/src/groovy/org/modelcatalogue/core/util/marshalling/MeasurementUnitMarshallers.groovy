package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.ValueDomain

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
        ret.valueDomains = [count: unit.valueDomains?.size() ?: 0, itemType: ValueDomain.name, link: "/${GrailsNameUtils.getPropertyName(unit.getClass())}/$unit.id/valueDomain"]
        return ret
    }

    protected void buildXml(el, XML xml) {
        super.buildXml(el, xml)
        xml.build {
            symbol el.symbol
        }
    }
}
