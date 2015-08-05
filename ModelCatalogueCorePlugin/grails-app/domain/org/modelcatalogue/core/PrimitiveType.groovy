package org.modelcatalogue.core

class PrimitiveType extends DataType {

    MeasurementUnit measurementUnit

    static constraints = {
        measurementUnit nullable: true, fetch: 'join'
    }

}
