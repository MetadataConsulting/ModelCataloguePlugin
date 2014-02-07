package uk.co.mc.core.util.marshalling
import grails.converters.JSON
import uk.co.mc.core.DataElement
import uk.co.mc.core.Relationship

class DataElementMarshaller  {
	
	void register() {
		JSON.registerObjectMarshaller(DataElement) { DataElement dataElement ->

			return [
			   id: dataElement.id,
               name: dataElement.name,
               description: dataElement.description,
               status: dataElement.status,
               versionNumber: dataElement.versionNumber,
               incomingRelationships: marshallRelationship(dataElement.incomingRelationships),
               outgoingRelationships: marshallRelationship(dataElement.outgoingRelationships)

			]

		}
	}

    def marshallRelationship(Set<Relationship> relationships){

        relationships.each{ Relationship relationship ->
            def path = relationship.destination.class.getSimpleName() + "/" + relationship.destination.id
            println("href: /$path")
        }

        return relationships

    }
}




