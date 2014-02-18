package uk.co.mc.core.util.marshalling

import grails.converters.XML
import uk.co.mc.core.DataElement

class DataElementMarshaller extends ExtendibleElementMarshallers {

    DataElementMarshaller() {
        super(DataElement)
    }

    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        ret.putAll code: element.code
        return ret
    }


    protected void buildXml(element, XML xml) {
        super.buildXml(element, xml)
        xml.build {
            code element.code
        }
    }
}




