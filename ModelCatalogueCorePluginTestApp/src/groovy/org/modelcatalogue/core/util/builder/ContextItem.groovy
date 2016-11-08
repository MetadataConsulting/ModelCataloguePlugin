package org.modelcatalogue.core.util.builder

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement

@CompileStatic
class ContextItem<T extends CatalogueElement> {
    CatalogueElementProxy<T> element
    Closure relationshipConfiguration
}
