package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import org.modelcatalogue.core.DataElement

class DataElementMarshaller extends PublishedElementMarshallers {

    DataElementMarshaller() {
        super(DataElement)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = super.prepareJsonMap(el)
        ret.putAll valueDomain: minimalCatalogueElementJSON(el.valueDomain)
        ret
    }

    protected void buildXml(el, XML xml) {
        super.buildXml(el, xml)
        xml.build {
            valueDomain el.valueDomain
        }
    }

}




