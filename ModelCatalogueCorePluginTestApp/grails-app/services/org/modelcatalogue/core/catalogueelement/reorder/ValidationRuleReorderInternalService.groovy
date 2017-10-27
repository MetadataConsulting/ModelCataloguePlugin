package org.modelcatalogue.core.catalogueelement.reorder

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.persistence.ValidationRuleGormService

@CompileStatic
class ValidationRuleReorderInternalService extends AbstractReorderInternalService {
    ValidationRuleGormService validationRuleGormService

    @Override
    CatalogueElement findById(Long id) {
        validationRuleGormService.findById(id)
    }
}
