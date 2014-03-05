package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import org.modelcatalogue.core.EnumeratedType

class EnumeratedTypeMarshaller extends CatalogueElementMarshallers {

    EnumeratedTypeMarshaller() {
        super(EnumeratedType)
    }

    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        ret.putAll enumerations: element.enumerations
        return ret
    }


    protected void buildXml(element, XML xml) {
        super.buildXml(element, xml)
        xml.build {
            enumerations element.enumerations
        }
    }

}




