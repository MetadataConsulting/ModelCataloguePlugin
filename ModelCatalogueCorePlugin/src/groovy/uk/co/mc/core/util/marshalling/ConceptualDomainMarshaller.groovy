package uk.co.mc.core.util.marshalling

import grails.converters.JSON
import grails.converters.XML
import uk.co.mc.core.ConceptualDomain

class ConceptualDomainMarshaller extends CatalogueElementMarshallers {

    ConceptualDomainMarshaller() {
        super(ConceptualDomain)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = super.prepareJsonMap(el)
        ret
    }

    protected void buildXml(el, XML xml) {
        super.buildXml(el, xml)
        xml.build {
        }
    }

}




