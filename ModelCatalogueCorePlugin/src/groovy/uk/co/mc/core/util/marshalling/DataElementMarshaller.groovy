package uk.co.mc.core.util.marshalling

import grails.converters.JSON
import grails.converters.XML
import uk.co.mc.core.DataElement

class DataElementMarshaller implements MarshallersProvider {

    void register() {
        JSON.registerObjectMarshaller(DataElement) { DataElement element ->
            def ret = [code: element.code]
            ret.putAll(CatalogueElementMarshallers.prepareJsonMap(element))
            return ret
        }
        XML.registerObjectMarshaller(DataElement) { DataElement el, XML xml ->
            CatalogueElementMarshallers.buildXml(el, xml)
            xml.build {
                code el.code
            }
        }
    }



//    void register() {
//        JSON.registerObjectMarshaller(DataElement) { DataElement dataElement ->
//
//            //marshall incoming and outgoing relationships
//
//			return [
//			   id: dataElement.id,
//               name: dataElement.name,
//               description: dataElement.description,
//               status: dataElement.status,
//               versionNumber: dataElement.versionNumber,
//               incomingRelationships: MarshallerUtils.marshallIncomingRelationships(dataElement),
//               outgoingRelationships: MarshallerUtils.marshallOutgoingRelationships(dataElement)
//			]
//
//		}
//
//	}

}




