package org.modelcatalogue.core.mappingsuggestions

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataElement
import groovy.transform.CompileStatic

@CompileStatic
class CatalogueElementWithNameAdapter implements WithName {

    CatalogueElement catalogueElement

    CatalogueElementWithNameAdapter(CatalogueElement catalogueElement) {
        this.catalogueElement = catalogueElement
    }

    @Override
    String getName() {
        catalogueElement.name
    }
}
