package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.ValueDomain

class ValueDomainMarshaller extends ExtendibleElementMarshallers {

    ValueDomainMarshaller() {
        super(ValueDomain)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = super.prepareJsonMap(el)
        ret.putAll unitOfMeasure: el.unitOfMeasure,
                rule: el.rule,
                dataType: el.dataType,
                multiple: el.multiple ?: false,
                mappings: [count: el.outgoingMappings?.size() ?: 0, itemType: Mapping.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/mapping"]
        ret
    }

    protected void buildXml(el, XML xml) {
        super.buildXml(el, xml)
        xml.build {
            unitOfMeasure el.unitOfMeasure
            rule el.rule
            dataType el.dataType
            mappings count: el.outgoingMappings?.size() ?: 0, itemType: Mapping.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/mapping"
        }
    }

    @Override
    protected void addXmlAttributes(Object el, XML xml) {
        super.addXmlAttributes(el, xml)
        xml.attribute('multiple', el.multiple ? "true" : "false")
    }
}




