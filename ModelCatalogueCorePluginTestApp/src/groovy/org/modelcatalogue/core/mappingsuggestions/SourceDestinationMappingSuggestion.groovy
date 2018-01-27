package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataElement

@CompileStatic
class SourceDestinationMappingSuggestion {
    CatalogueElement source
    CatalogueElement destination
    float distance
}
