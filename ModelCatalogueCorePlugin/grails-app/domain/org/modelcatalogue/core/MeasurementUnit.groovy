package org.modelcatalogue.core
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

    static constraints = {
        name unique: 'versionNumber'
        symbol nullable: true, size: 1..100
    }

    static transients = ['valueDomains']

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

    List<ValueDomain> getValueDomains() {
        if (!readyForQueries) {
            return []
        }
        return ValueDomain.findAllByUnitOfMeasure(this)
    }

    Long countValueDomains() {
        if (!readyForQueries) {
            return 0
        }
        return ValueDomain.countByUnitOfMeasure(this)
    }

}
