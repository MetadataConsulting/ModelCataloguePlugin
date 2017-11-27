package org.modelcatalogue.core.events

import org.modelcatalogue.core.CatalogueElement

class SourceDestinationEvent implements MetadataResponseSuccessEvent {
    CatalogueElement source
    CatalogueElement destination
}
