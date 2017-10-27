package org.modelcatalogue.core.catalogueelement.searchwithinrelationships

import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Tag
import org.modelcatalogue.core.persistence.TagGormService

@CompileStatic
class TagSearchWithinRelationshipsService extends AbstractSearchWithinRelationshipsService {

    TagGormService tagGormService

    @Override
    CatalogueElement findById(Long id) {
        tagGormService.findById(id)
    }

    @Override
    protected String resourceName() {
        GrailsNameUtils.getPropertyName(Tag.class.name)
    }
}
