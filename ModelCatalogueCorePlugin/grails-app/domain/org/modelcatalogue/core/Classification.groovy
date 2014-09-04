package org.modelcatalogue.core

import grails.util.GrailsNameUtils


class Classification extends CatalogueElement {

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    Set classifies = []

    String urlName

    static searchable = {
        name boost:5
        except = ['incomingRelationships', 'outgoingRelationships']
    }

    static hasMany = [classifies: PublishedElement]

    static constraints = {
        urlName size: 1..255, bindable: false
        name validator: { val, obj ->
            if (!val) return true
            if (GrailsNameUtils.getPropertyName(val) in ['catalogue', 'all']) {
                return "$val is prohibited name of the Classification"
            }
            return true
        }
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

    def beforeInsert() {
        beforeUpdate()
    }

    def beforeUpdate () {
        // the url should be persistent, you should always
        if (name && !urlName) {
            urlName = GrailsNameUtils.getPropertyName(name)
        }
    }

}
