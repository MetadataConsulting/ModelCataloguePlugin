package org.modelcatalogue.core.catalogueelement.searchwithinrelationships

import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ValidationRule
import org.modelcatalogue.core.persistence.ValidationRuleGormService

@CompileStatic
class ValidationRuleSearchWithinRelationshipsService extends AbstractSearchWithinRelationshipsService {

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
