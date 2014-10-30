package org.modelcatalogue.core


class Classification extends CatalogueElement {

    String namespace

    static constraints = {
        namespace nullable: true, unique: true
    }

    static searchable = {
        name boost:5
        except = ['incomingRelationships', 'outgoingRelationships']
    }

    static relationships = [
            outgoing: [classification: 'classifies']
    ]

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

}
