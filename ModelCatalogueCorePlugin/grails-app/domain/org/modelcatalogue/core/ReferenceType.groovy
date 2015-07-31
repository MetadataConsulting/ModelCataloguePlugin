package org.modelcatalogue.core

class ReferenceType extends DataType {

    DataClass dataClass

    static constraints = {
        dataClass nullable: true, fetch: 'join'
    }
}
