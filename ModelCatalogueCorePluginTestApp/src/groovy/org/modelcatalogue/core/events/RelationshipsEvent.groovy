package org.modelcatalogue.core.events

import groovy.transform.CompileStatic
import org.modelcatalogue.core.util.lists.Relationships

@CompileStatic
class RelationshipsEvent implements MetadataResponseSuccessEvent {
    Relationships relationships
}
