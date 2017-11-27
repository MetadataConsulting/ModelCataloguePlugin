package org.modelcatalogue.core.events

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement

@CompileStatic
class CatalogueElementWithErrorsEvent implements MetadataResponseFailureEvent {
    CatalogueElement catalogueElement
}
