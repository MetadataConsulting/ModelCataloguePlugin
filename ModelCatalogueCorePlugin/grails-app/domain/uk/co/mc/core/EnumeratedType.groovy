package uk.co.mc.core

class EnumeratedType extends DataType{

    Set <String> enumerations

    static constraints = {
        enumerations minSize: 2, nullable: false
    }
}
