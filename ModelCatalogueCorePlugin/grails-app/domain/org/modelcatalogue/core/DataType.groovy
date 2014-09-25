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

    static hasMany  = [relatedValueDomains: ValueDomain]

    static searchable = {
        name boost:5
        except = ['relatedValueDomains', 'incomingRelationships', 'outgoingRelationships']
    }

    static constraints = {
        name size: 1..255
    }

    static mapping = {
        tablePerHierarchy false
        relatedValueDomains cascade: "all-delete-orphan"
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

    private final static defaultRelationshipTypesDefinitions = [
            [name: "String", description: "java.lang.String"],
            [name: "Integer", description: "java.lang.Integer"],
            [name: "Double", description: "java.lang.Double"],
            [name: "Boolean", description: "java.lang.Boolean"],
            [name: "Date", description: "java.util.Date"],
            [name: "Time", description: "java.sql.Time"],
            [name: "Currency", description: "java.util.Currency"]
    ]

    static initDefaultDataTypes() {
        for (definition in defaultRelationshipTypesDefinitions) {
            DataType existing = findByName(definition.name)
            if (!existing) {
                new DataType(definition).save()
            }
        }
    }


}
