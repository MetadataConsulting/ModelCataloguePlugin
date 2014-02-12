package uk.co.mc.core

/*
* A Data Type is like a primitive type
* i.e. integer, string, byte, boolean, time........
* additional types can be specified (as well as enumerated types (see EnumeratedType))
* Data Types are used by Value Domains (please see ValueDomain and Usance)
*/

class DataType extends CatalogueElement {

    static constraints = {
        name unique: true, size: 2..255
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

}
