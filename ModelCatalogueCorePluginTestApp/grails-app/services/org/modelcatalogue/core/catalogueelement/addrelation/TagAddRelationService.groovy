package org.modelcatalogue.core.catalogueelement.addrelation

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence. TagGormService

@CompileStatic
class  TagAddRelationService extends AbstractAddRelationService {

    TagGormService  tagGormService

    @Override
    CatalogueElement findById(Long id) {
        tagGormService.findById(id)
    }
}
