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

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    def grailsApplication

    static hasMany  = [valueDomains: ValueDomain]

    static searchable = {
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


}
