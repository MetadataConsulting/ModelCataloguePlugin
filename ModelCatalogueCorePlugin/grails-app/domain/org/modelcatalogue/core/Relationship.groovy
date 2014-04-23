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
        | Model            | [heirachical]    | Model       | "parentOf"             | "ChildOf"            |
        | DataElement      | [supersession]   | DataElement | "supercedes"           | "supercededBy"       |

*
* New types can be created using the ontology type class
*
*/

class Relationship {

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

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

    Boolean archived = false

    static belongsTo = [source: CatalogueElement, destination: CatalogueElement]

    static constraints = {
        relationshipType unique: ['source', 'destination'], validator: { val, obj ->

            if (!val) return true;

            String errorMessage = val.validateSourceDestination(obj.source, obj.destination)
            if (errorMessage) {
                return errorMessage;
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

    String toString() {
        "${getClass().simpleName}[id: ${id}, source: ${source}, destination: ${destination}, type: ${relationshipType?.name}]"
    }

    def beforeDelete(){
        if (source || destination) {
            destination?.removeFromIncomingRelationships(this)
            source?.removeFromOutgoingRelationships(this)
        }
    }

}
