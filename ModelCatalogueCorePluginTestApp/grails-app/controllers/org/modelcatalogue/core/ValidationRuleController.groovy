package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.catalogueelement.ManageCatalogueElementService
import org.modelcatalogue.core.catalogueelement.ValidationRuleCatalogueElementService
import org.modelcatalogue.core.persistence.ValidationRuleGormService
import org.modelcatalogue.core.scripting.Validating
import org.modelcatalogue.core.scripting.ValidatingImpl
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.MetadataDomainEntity
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.lists.Relationships
import org.modelcatalogue.core.validation.ValidationRuleJsonView
import org.modelcatalogue.core.validation.ValidationRulesJsonView

class ValidationRuleController extends AbstractCatalogueElementController<ValidationRule> {

    ValidationRuleGormService validationRuleGormService
    ValidationRuleCatalogueElementService validationRuleCatalogueElementService
    ValidationRuleService validationRuleService

    ValidationRuleController() {
        super(ValidationRule, false)
    }

    def content(Integer max) {
        handleParams(max)

        params.sort = 'incomingIndex'

        ValidationRule element = findById(params.long('id'))

        if (!element) {
            notFound()
            return
        }

        DataModelFilter filter = overridableDataModelFilter

        respond new Relationships(
            owner: element,
            direction: RelationshipDirection.OUTGOING,
            list: Lists.fromCriteria(params, Relationship, "/${resourceName}/${params.id}/content") {
                join 'destination'
                eq 'source', element
                inList 'relationshipType', [RelationshipType.involvednessType, RelationshipType.ruleContextType]

                if (filter) {
                    or {
                        isNull 'dataModel'
                        and {
                            if (filter.excludes) {
                                not {
                                    'in' 'dataModel.id', filter.excludes
                                }
                            }
                            if (filter.includes) {
                                'in'  'dataModel.id', filter.includes
                            }
                        }
                    }
                }

                sort('incomingIndex')
            }
        )
    }

    def rules(String gormUrl) {

        MetadataDomainEntity metadataDomainEntity = MetadataDomainEntity.of(gormUrl)

        if ( metadataDomainEntity == null ) {
            render status: 404
            return
        }
        ValidationRulesJsonView validationRulesJsonView = validationRuleService.findValidationRulesByMetadataDomainEntity(metadataDomainEntity)
        if ( validationRulesJsonView == null ) {
            render status: 204
            return
        }
        respond validationRulesJsonView
    }

    protected ValidationRule findById(long id) {
        validationRuleGormService.findById(id)
    }

    @Override
    protected ManageCatalogueElementService getManageCatalogueElementService() {
        validationRuleCatalogueElementService
    }

    @Override
    protected bindRelations(ValidationRule instance, boolean newVersion, Object objectToBind) {
        super.bindRelations(instance, newVersion, objectToBind)
        def dataClasses = objectToBind.dataClasses ?: []

        for (dataClass in dataClasses) {
            DataClass context = DataClass.get(dataClass.id)
            instance.addToAppliedWithin(context)
        }

        def dataElements = objectToBind.dataElements ?: []
        for (dataElement in dataElements) {
            DataElement context = DataElement.get(dataElement.id)
            instance.addToInvolves(context)
        }
    }
}
