package org.modelcatalogue.core


class Classification extends CatalogueElement {

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    Set classifies = []

    static searchable = {
        name boost:5
        except = ['incomingRelationships', 'outgoingRelationships']
    }

    static hasMany = [classifies: PublishedElement]

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

}
