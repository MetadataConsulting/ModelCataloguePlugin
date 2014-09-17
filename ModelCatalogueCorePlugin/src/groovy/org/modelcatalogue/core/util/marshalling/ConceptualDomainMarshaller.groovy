package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.ValueDomain

class ConceptualDomainMarshaller extends CatalogueElementMarshallers {

    ConceptualDomainMarshaller() {
        super(ConceptualDomain)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = super.prepareJsonMap(el)
        ret.namespace = el.namespace
        ret.valueDomains = [count: el.valueDomains?.size() ?: 0, itemType: ValueDomain.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/valueDomain"]
        ret
    }

    protected void buildXml(el, XML xml) {
        super.buildXml(el, xml)
        xml.build {
            namespace el.namespace
            valueDomains count: el.valueDomains?.size() ?: 0, itemType: ValueDomain.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/valueDomain"
        }
    }

}




