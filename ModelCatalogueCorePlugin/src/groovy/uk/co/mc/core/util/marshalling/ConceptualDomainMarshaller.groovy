package uk.co.mc.core.util.marshalling

import grails.converters.JSON
import grails.converters.XML
import uk.co.mc.core.ConceptualDomain

class ConceptualDomainMarshaller implements MarshallersProvider {

    void register() {
        JSON.registerObjectMarshaller(ConceptualDomain) { ConceptualDomain element ->
            def ret = []
            ret.putAll(CatalogueElementMarshallers.prepareJsonMap(element))
            return ret
        }
        XML.registerObjectMarshaller(ConceptualDomain) { ConceptualDomain el, XML xml ->
            CatalogueElementMarshallers.buildXml(el, xml)
            xml.build {}
        }
    }

}




