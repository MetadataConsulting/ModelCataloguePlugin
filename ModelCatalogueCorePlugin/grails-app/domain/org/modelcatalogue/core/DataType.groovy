package org.modelcatalogue.core

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/*
* A Data Type is like a primitive type
* i.e. integer, string, byte, boolean, time........
* additional types can be specified (as well as enumerated types (see EnumeratedType))
* Data Types are used by Value Domains (please see ValueDomain and Usance)
*/


class DataType extends CatalogueElement {

    static elasticGormSearchable = {
        name boost:5
        incomingRelationships component: true
        outgoingRelationships component: true
    }

    static constraints = {
        name unique: true, size: 2..255
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DataType)) {
            return false;
        }
        if (this.is(obj)) {
            return true;
        }
        DataType ce = (DataType) obj;
        return new EqualsBuilder()
                .append(name, ce.name)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .toHashCode();
    }


    private final static defaultRelationshipTypesDefinitions = [
            [name: "String", description: "java.lang.String"],
            [name: "Integer", description: "java.lang.Integer"],
            [name: "Double", description: "java.lang.String"],
            [name: "Boolean", description: "java.lang.Boolean"],
            [name: "Date", description: "java.util.Date"],
            [name: "Time", description: "java.sql.Time"],
            [name: "Currency", description: "java.util.Currency"]
    ]

    static initDefaultDataTypes() {
        for (definition in defaultRelationshipTypesDefinitions) {
            DataType existing = DataType.findByName(definition.name)
            if (!existing) {
                new DataType(definition).save()
            }
        }
    }


}
