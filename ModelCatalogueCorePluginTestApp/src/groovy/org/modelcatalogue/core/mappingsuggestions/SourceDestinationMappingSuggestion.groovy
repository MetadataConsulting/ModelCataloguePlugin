package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic
import groovy.transform.Sortable
import org.modelcatalogue.core.CatalogueElement

@Sortable(includes = 'distance')
@CompileStatic
class SourceDestinationMappingSuggestion {
    CatalogueElement source
    CatalogueElement destination
    Float distance
}
