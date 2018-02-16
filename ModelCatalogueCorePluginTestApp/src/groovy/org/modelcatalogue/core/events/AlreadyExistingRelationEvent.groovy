package org.modelcatalogue.core.events

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.util.RelationshipDirection

@CompileStatic
class AlreadyExistingRelationEvent implements MetadataResponseFailureEvent {
    CatalogueElement source
    Relationship rel
    RelationshipDirection direction
}
