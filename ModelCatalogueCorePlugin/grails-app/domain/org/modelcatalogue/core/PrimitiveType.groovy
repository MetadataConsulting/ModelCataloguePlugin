package org.modelcatalogue.core

class PrimitiveType extends DataType {

    MeasurementUnit measurementUnit

    static constraints = {
        measurementUnit nullable: true, fetch: 'join'
    }

    static mapping = {
        measurementUnit lazy: false
    }

    static fetchMode = [measurementUnit: 'eager']

    List<String> getInheritedAssociationsNames() { ['measurementUnit'] }

}
