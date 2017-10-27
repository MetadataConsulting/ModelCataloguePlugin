package org.modelcatalogue.core.catalogueelement.reorder

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.TagGormService

@CompileStatic
class TagReorderInternalService extends AbstractReorderInternalService {
    TagGormService tagGormService

    @Override
    CatalogueElement findById(Long id) {
        tagGormService.findById(id)
    }
}
