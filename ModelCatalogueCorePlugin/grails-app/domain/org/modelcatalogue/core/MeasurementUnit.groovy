package org.modelcatalogue.core

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/*
* Measurement units are units such as MPH, cm3, cm2, m etc.
* They are used by value domains to instantiate a data element
*
* */

class MeasurementUnit extends PublishedElement {

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
        name boost:5
        except = ['incomingRelationships', 'outgoingRelationships']
    }

    String symbol

    static hasMany = [valueDomains: ValueDomain]

    static constraints = {
        name unique: 'versionNumber'
        symbol nullable: true, size: 1..100
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

}
