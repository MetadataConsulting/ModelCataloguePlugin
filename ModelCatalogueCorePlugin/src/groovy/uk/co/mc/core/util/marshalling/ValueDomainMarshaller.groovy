package uk.co.mc.core.util.marshalling

import grails.converters.JSON
import grails.converters.XML
import uk.co.mc.core.ValueDomain

class ValueDomainMarshaller implements MarshallersProvider {

    void register() {
        JSON.registerObjectMarshaller(ValueDomain) { ValueDomain element ->
            def ret = [unitOfMeasure: element.unitOfMeasure, regexDef: element.regexDef, dataType: element.dataType]
            ret.putAll(CatalogueElementMarshallers.prepareJsonMap(element))
            return ret
        }
        XML.registerObjectMarshaller(ValueDomain) { ValueDomain el, XML xml ->
            CatalogueElementMarshallers.buildXml(el, xml)
            xml.build {
                unitOfMeasure el.unitOfMeasure
                regexDef el.regexDef
                dataType el.dataType

            }
        }
    }

}




