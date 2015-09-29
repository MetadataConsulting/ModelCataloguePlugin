package org.modelcatalogue.core.util.builder

import org.modelcatalogue.core.CatalogueElement

class ContextItem<T extends CatalogueElement> {
    CatalogueElementProxy<T> element
    Closure relationshipConfiguration
}
