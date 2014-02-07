package uk.co.mc.core.util.marshalling
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import grails.converters.JSON
import uk.co.mc.core.DataElement
import uk.co.mc.core.Relationship

class DataElementMarshaller {
	
	void register() {
		JSON.registerObjectMarshaller(DataElement) { DataElement dataElement ->

            //marshall incoming and outgoing relationships

			return [
			   id: dataElement.id,
               name: dataElement.name,
               description: dataElement.description,
               status: dataElement.status,
               versionNumber: dataElement.versionNumber,
               incomingRelationships: MarshallerUtils.marshallIncomingRelationships(dataElement),
               outgoingRelationships: MarshallerUtils.marshallOutgoingRelationships(dataElement),

			]

		}
	}

}




