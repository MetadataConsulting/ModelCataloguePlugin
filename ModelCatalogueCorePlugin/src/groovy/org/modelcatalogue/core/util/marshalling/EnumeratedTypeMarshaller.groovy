package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.ValueDomain

class EnumeratedTypeMarshaller extends PublishedElementMarshallers {

    EnumeratedTypeMarshaller() {
        super(EnumeratedType)
    }

    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        ret.putAll valueDomains: [count: element.relatedValueDomains?.size() ?: 0, itemType: ValueDomain.name, link: "/${GrailsNameUtils.getPropertyName(element.getClass())}/$element.id/valueDomain"]
        ret.putAll enumerations: element.enumerations
        ret
    }


    protected void buildXml(element, XML xml) {
        super.buildXml(element, xml)
        xml.build {
            valueDomains count: element.relatedValueDomains?.size() ?: 0, itemType: ValueDomain.name, link: "/${GrailsNameUtils.getPropertyName(element.getClass())}/$element.id/valueDomain"
            enumerations {
                for (e in element.enumerations) {
                    enumeration key: e.key, e.value
                }
            }
        }
    }

}




