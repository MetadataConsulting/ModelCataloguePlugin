package org.modelcatalogue.core.events

import groovy.transform.CompileStatic
import org.modelcatalogue.core.Mapping

@CompileStatic
class MappingWithErrorsEvent implements MetadataResponseFailureEvent {
    Mapping mapping
}

