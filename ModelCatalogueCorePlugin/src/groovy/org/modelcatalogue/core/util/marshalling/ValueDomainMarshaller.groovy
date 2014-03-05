package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.ValueDomain

class ValueDomainMarshaller extends CatalogueElementMarshallers {

    ValueDomainMarshaller() {
        super(ValueDomain)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = Object.prepareJsonMap(el)
        ret.putAll unitOfMeasure: el.unitOfMeasure,
                regexDef: el.regexDef,
                dataType: el.dataType,
                mappings: [count: el.outgoingMappings?.size() ?: 0, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/mapping"]
        ret
    }

    protected void buildXml(el, XML xml) {
        Object.buildXml(el, xml)
        xml.build {
            unitOfMeasure el.unitOfMeasure
            regexDef el.regexDef
            dataType el.dataType
            mappings count: el.outgoingMappings?.size() ?: 0, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/mapping"
        }
    }
}




