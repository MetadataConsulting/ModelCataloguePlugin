package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.Tag

class TagMarshaller extends CatalogueElementMarshaller {

    TagMarshaller() {
        super(Tag)
    }
}
