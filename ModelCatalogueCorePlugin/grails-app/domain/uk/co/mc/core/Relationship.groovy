package uk.co.mc.core

/*
* Users can create relationships between all catalogue elements. They include
* DataType, ConceptualDomain, MeasurementUnit, Model, ValueDomain, DataElement
* Relationshipss have a source element, a destination element and a relationship type.
* There are a number of different predefined relationship types that describe the ways catalogue
* elements are related in the model catalogue

        * ----------------- ------------------ ------------- -----------------------  ----------------------
        | Source           | Relationship     | Destination | Source->Destination    |  Destination<-Source |
        | ---------------- | -----------------| ----------- | ---------------------- | -------------------- |
        | ConceptualDomain |  [context]       |  Model      | "provides context for" | "has context of"     |
        | ConceptualDomain | [inclusion]      | ValueDomain |  "includes"            | "included in"        |
        | Model            | [containment]    | DataElement |  "contains"            |  "contained in"      |
        | DataElement      | [instantiation]  | ValueDomain | "instantiated by"      | "instantiates"       |
        | ValueDomain      | [usance]         | DataType    | "uses"                 | "usedBy"             |
        | DataType         | [mapping]        | DataType    | "mapsTo"               |  "mapsTo"            |
        | Model            | [heirachical]    | Model       | "parentOf"             | "ChildOf"            |
        | DataElement      | [supersession]   | DataElement | "supercedes"           | "supercededBy"       |

*
* New types can be created using the ontology type class
*
*/

class Relationship {

    CatalogueElement source

//    TODO: save the cardinality for source and destination somehow
//    e.g. following
//
//    Range sourceCardinality = 0..1
//    Range destinatinCardinality = 1..Integer.MAX_VALUE

    CatalogueElement destination

    RelationshipType relationshipType

    static constraints = {
        relationshipType validator: { val, obj ->

            if (!val) return true;
            if (!val.validateSourceDestination(obj.source, obj.destination)) {
                return false;
            }
            return true;

        }
    }

    static Relationship link(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType) {


        if (source.id && destination.id && relationshipType.id) {

            Relationship relationshipInstance = findBySourceAndDestinationAndRelationshipType(source, destination, relationshipType)

            if (relationshipInstance) {

                return relationshipInstance

            }

        }

        Relationship relationshipInstance = new Relationship(
                source: source.id ? source : null,
                destination: destination.id ? destination : null,
                relationshipType: relationshipType.id ? relationshipType : null
        )

        source.addToOutgoingRelationships(relationshipInstance)
        destination.addToIncomingRelationships(relationshipInstance)

        relationshipInstance.save(flush: true)


        relationshipInstance


    }


    static Relationship unlink(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType) {
        if (source?.id && destination?.id && relationshipType?.id) {

            Relationship relationshipInstance = findBySourceAndDestinationAndRelationshipType(source, destination, relationshipType)

            if (relationshipInstance) {

                source.removeFromOutgoingRelationships(relationshipInstance)
                destination.removeFromIncomingRelationships(relationshipInstance)
                relationshipInstance.delete()
            }

        }
        return null
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, source: ${source}, destination: ${destination}, type: ${relationshipType?.name}]"
    }

}
