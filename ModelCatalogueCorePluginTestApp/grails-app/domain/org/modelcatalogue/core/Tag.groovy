package org.modelcatalogue.core

class Tag extends CatalogueElement {

    static relationships = [
        outgoing: [tag: 'tags']
    ]
    

    @Override
    Map<CatalogueElement, Object> manualDeleteRelationships(DataModel toBeDeleted) {
        return [:]
    }
}
