package org.modelcatalogue.core
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

    static searchable = {
        except = ['source', 'destination']
        relationshipType component:true
    }

    CatalogueElement source
    CatalogueElement destination

    RelationshipType relationshipType

    // cardinality
    Integer sourceMinOccurs
    Integer sourceMaxOccurs
    Integer destinationMinOccurs
    Integer destinationMaxOccurs

    static belongsTo = [source: CatalogueElement, destination: CatalogueElement]

    static constraints = {
        relationshipType unique: ['source', 'destination'], validator: { val, obj ->

            if (!val) return true;
            if (!val.validateSourceDestination(obj.source, obj.destination)) {
                return false;
            }
            return true;

        }
        sourceMinOccurs nullable: true, min: 0, validator: { val, obj ->
            if (!val) return true
            if (!obj.sourceMaxOccurs) return true
            if (val > obj.sourceMaxOccurs) return false
            return true
        }
        sourceMaxOccurs nullable: true, min: 1
        destinationMinOccurs nullable: true, min: 0, validator: { val, obj ->
            if (!val) return true
            if (!obj.destinationMaxOccurs) return true
            if (val > obj.destinationMaxOccurs) return false
            return true
        }
        destinationMaxOccurs nullable: true, min: 1
    }

    static Relationship link(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType) {
        if (source?.id && destination?.id && relationshipType?.id) {

            Relationship relationshipInstance = findBySourceAndDestinationAndRelationshipType(source, destination, relationshipType)

            if (relationshipInstance) {

                return relationshipInstance

            }

        }

        Relationship relationshipInstance = new Relationship(
                source: source?.id ? source : null,
                destination: destination?.id ? destination : null,
                relationshipType: relationshipType?.id ? relationshipType : null
        )

        source?.addToOutgoingRelationships(relationshipInstance)
        destination?.addToIncomingRelationships(relationshipInstance)

        relationshipInstance.save(flush: true)


        relationshipInstance


    }


    static Relationship unlink(CatalogueElement source, CatalogueElement destination, RelationshipType relationshipType) {
        if (source?.id && destination?.id && relationshipType?.id) {
            Relationship relationshipInstance = findBySourceAndDestinationAndRelationshipType(source, destination, relationshipType)
            if (relationshipInstance && source && destination) {
                destination?.removeFromIncomingRelationships(relationshipInstance)
                source?.removeFromOutgoingRelationships(relationshipInstance)
                relationshipInstance.delete(flush: true)
                return relationshipInstance
            }
        }
        return null
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, source: ${source}, destination: ${destination}, type: ${relationshipType?.name}]"
    }

    def beforeDelete(){
        if (source && destination) {
            destination?.removeFromIncomingRelationships(this)
            source?.removeFromOutgoingRelationships(this)
        }
    }

}
