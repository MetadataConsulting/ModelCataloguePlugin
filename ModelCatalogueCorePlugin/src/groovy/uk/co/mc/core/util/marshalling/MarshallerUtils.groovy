package uk.co.mc.core.util.marshalling

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject
import uk.co.mc.core.CatalogueElement
import uk.co.mc.core.Relationship

/**
 * Created by adammilward on 07/02/2014.
 */
class MarshallerUtils {

    static marshallIncomingRelationships(CatalogueElement element){
        Set marshalledRelationships = []

        element?.incomingRelationships.each{ relationship ->

            def marshalledRelationship = new JSONObject()

            marshalledRelationship.destinationPath = "/" + relationship.destination.class.getSimpleName() + "/" + relationship.destination.id
            marshalledRelationship.destinationName = relationship.destination.name
            marshalledRelationship.sourcePath = "/" + element.class.getSimpleName() + "/" + element.id
            marshalledRelationship.sourceName = element.name
            marshalledRelationship.relationshipType = relationship.relationshipType
            marshalledRelationships.add(marshalledRelationship)

        }

        return marshalledRelationships
    }

    static marshallOutgoingRelationships(CatalogueElement element){

        Set marshalledRelationships = []

        element?.outgoingRelationships.each{ relationship ->

            def marshalledRelationship = new JSONObject()

            marshalledRelationship.sourcePath = "/" + relationship.destination.class.getSimpleName() + "/" + relationship.destination.id
            marshalledRelationship.sourceName = relationship.destination.name
            marshalledRelationship.destinationPath = "/" + element.class.getSimpleName() + "/" + element.id
            marshalledRelationship.destinationName = element.name
            marshalledRelationship.relationshipType = relationship.relationshipType

            marshalledRelationships.add(marshalledRelationship)

        }

        return marshalledRelationships
    }


}
