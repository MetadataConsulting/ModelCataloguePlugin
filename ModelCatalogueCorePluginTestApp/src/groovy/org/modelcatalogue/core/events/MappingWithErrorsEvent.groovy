package org.modelcatalogue.core.events
import groovy.transform.CompileStatic
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.Relationship

@CompileStatic
class MappingWithErrorsEvent implements MetadataResponseFailureEvent {
    Mapping mapping
}

