package uk.co.mc.core

/*
* There are a number of different predefined relationship types that describe the ways catalogue
* elements are related in the model catalogue
*
*  -----------------------------------------------------------------------------------------------------------
*  | Relationship Type |  Source              | Destination | Source->Destination    |  Destination<-Source |
*  | ----------------- | ---------------------| ----------- | ---------------------- | -------------------- |
*  |  [context]        |  ConceptualDomain    |  Model      | "provides context for" | "has context of"     |
*  | [inclusion]       |  ConceptualDomain    | ValueDomain |  "includes"            | "included in"        |
*  | [containment]     |  Model               | DataElement |  "contains"            |  "contained in"      |
*  | [instantiation]   |  DataElement         | ValueDomain | "instantiated by"      | "instantiates"       |
*  | [mapping]         |  DataType            | DataType    | "mapsTo"               |  "mapsTo"            |
*  | [heirachical]     |  Model               | Model       | "parentOf"             | "ChildOf"            |
*  | [supersession]    |  DataElement         | DataElement | "supercedes"           | "supercededBy"       |
*  -----------------------------------------------------------------------------------------------------------------
*
* New types can be created using the ontology type class
*/


abstract class RelationshipType {

    boolean validateSourceDestination(source, destination){
        return true
    }

}
