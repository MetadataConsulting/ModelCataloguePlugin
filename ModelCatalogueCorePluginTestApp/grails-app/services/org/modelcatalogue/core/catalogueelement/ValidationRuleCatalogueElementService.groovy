package org.modelcatalogue.core.catalogueelement

import grails.util.GrailsNameUtils
import grails.validation.Validateable
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ValidationRule
import org.modelcatalogue.core.persistence.ValidationRuleGormService

@CompileStatic
class ValidationRuleCatalogueElementService<T extends CatalogueElement> extends AbstractCatalogueElementService<ValidationRule> {

    ValidationRuleGormService validationRuleGormService

    @Override
    protected String resourceName() {
        GrailsNameUtils.getPropertyName(ValidationRule.class.name)
    }

    @Override
    CatalogueElement findById(Long id) {
        validationRuleGormService.findById(id)
    }
}
