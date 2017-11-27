package org.modelcatalogue.core.events

import groovy.transform.CompileStatic
import org.modelcatalogue.core.Relationship

@CompileStatic
class RelationshipMovedEvent implements MetadataResponseSuccessEvent {
    Relationship relationship
}
