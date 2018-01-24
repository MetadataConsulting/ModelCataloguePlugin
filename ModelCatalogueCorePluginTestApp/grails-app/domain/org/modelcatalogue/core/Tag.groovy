package org.modelcatalogue.core

class Tag extends CatalogueElement {

    static relationships = [
        outgoing: [tag: 'tags']
    ]
}
