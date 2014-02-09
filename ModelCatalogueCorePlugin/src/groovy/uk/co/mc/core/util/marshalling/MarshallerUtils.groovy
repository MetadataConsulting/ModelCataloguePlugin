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

        def toMarshall = element?.incomingRelationships.collect{ relationship ->
            ["destinationPath": "/" + relationship.destination.class.getSimpleName() + "/" + relationship.destination.id,
             "destinationName":relationship.destination.name,
             "sourcePath": "/" + element.class.getSimpleName() + "/" + element.id,
             "sourceName": element.name,
             "relationshipType": relationship.relationshipType
            ]
        }

        return toMarshall
    }

    static marshallOutgoingRelationships(CatalogueElement element){

        def toMarshall = element?.incomingRelationships.collect{ relationship ->
            [       "sourcePath": "/" + relationship.destination.class.getSimpleName() + "/" + relationship.destination.id,
                    "sourceName":relationship.destination.name,
                    "destinationPath": "/" + element.class.getSimpleName() + "/" + element.id,
                    "destinationName": element.name,
                    "relationshipType": relationship.relationshipType
            ]
        }

        return toMarshall

    }


}
