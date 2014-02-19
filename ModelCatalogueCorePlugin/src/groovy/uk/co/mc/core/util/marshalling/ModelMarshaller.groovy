package uk.co.mc.core.util.marshalling

import grails.converters.XML
import uk.co.mc.core.Model

class ModelMarshaller extends ExtendibleElementMarshallers {

    ModelMarshaller() {
        super(Model)
    }

    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        return ret
    }


    protected void buildXml(element, XML xml) {
        super.buildXml(element, xml)
    }
}




