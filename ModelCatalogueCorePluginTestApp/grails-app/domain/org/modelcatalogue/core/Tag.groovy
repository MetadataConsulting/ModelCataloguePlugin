package org.modelcatalogue.core

class Tag extends CatalogueElement {

    static relationships = [
        outgoing: [tag: 'tags']
    ]

    static constraints = {
        name unique: 'versionNumber'
    }

    @Override
    Map<CatalogueElement, Object> manualDeleteRelationships(DataModel toBeDeleted) {
        return [:]
    }
}
