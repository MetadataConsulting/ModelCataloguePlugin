package org.modelcatalogue.core.catalogueelement.addrelation

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence. ValidationRuleGormService

@CompileStatic
class ValidationRuleAddRelationService extends AbstractAddRelationService {

    ValidationRuleGormService  validationRuleGormService

    @Override
    CatalogueElement findById(Long id) {
        validationRuleGormService.findById(id)
    }
}
