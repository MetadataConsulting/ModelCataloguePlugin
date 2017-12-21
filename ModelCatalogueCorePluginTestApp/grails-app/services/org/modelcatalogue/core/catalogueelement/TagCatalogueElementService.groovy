package org.modelcatalogue.core.catalogueelement

import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Tag
import org.modelcatalogue.core.persistence.TagGormService

@CompileStatic
class TagCatalogueElementService<T extends CatalogueElement> extends AbstractCatalogueElementService<Tag> {

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
