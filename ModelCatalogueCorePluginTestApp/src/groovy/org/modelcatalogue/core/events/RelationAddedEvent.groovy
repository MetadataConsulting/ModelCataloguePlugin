package org.modelcatalogue.core.events

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.util.RelationshipDirection

@CompileStatic
class RelationAddedEvent implements MetadataResponseSuccessEvent {
    CatalogueElement source
    Relationship rel
    RelationshipDirection direction
}
