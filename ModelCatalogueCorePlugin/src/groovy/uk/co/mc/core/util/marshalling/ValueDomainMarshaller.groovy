package uk.co.mc.core.util.marshalling

import grails.converters.XML
import uk.co.mc.core.ValueDomain

class ValueDomainMarshaller extends CatalogueElementMarshallers {

    ValueDomainMarshaller() {
        super(ValueDomain)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = super.prepareJsonMap(el)
        ret.putAll unitOfMeasure: el.unitOfMeasure, regexDef: el.regexDef, dataType: el.dataType
        ret
    }

    protected void buildXml(el, XML xml) {
        super.buildXml(el, xml)
        xml.build {
            unitOfMeasure el.unitOfMeasure
            regexDef el.regexDef
            dataType el.dataType

        }
    }
}




