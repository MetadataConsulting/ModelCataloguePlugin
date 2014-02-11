package uk.co.mc.core

/*
* A Data Type is like a primitive type
* i.e. integer, string, byte, boolean, time........
* additional types can be specified (as well as enumerated types (see EnumeratedType))
* Data Types are used by Value Domains (please see ValueDomain and Usance)
*/

class DataType extends CatalogueElement{

    static constraints = {
		name unique:true, size: 2..255
    }

    static transients = ['mapsTo', 'mapsFrom']

    //MAPPINGS


    List/*<DataType>*/ getMapsTo() {
        getOutgoingRelationsByType(RelationshipType.mappingType)
    }

    Relationship addToMapsTo(DataType dataType) {
        createLinkTo(dataType, RelationshipType.mappingType)
    }

    void removeFromMapsTo(DataType dataType) {
        removeLinkTo(dataType, RelationshipType.mappingType)
    }

    List/*<DataType>*/ getMapsFrom() {
        getIncomingRelationsByType(RelationshipType.mappingType)
    }

    Relationship addToMapsFrom(DataType dataType) {
        createLinkFrom(dataType, RelationshipType.mappingType)
    }

    void removeFromMapsFrom(DataType dataType) {
        removeLinkFrom(dataType, RelationshipType.mappingType)
    }

}
