package org.modelcatalogue.core

import org.modelcatalogue.core.catalogueelement.addrelation.AbstractAddRelationService
import org.modelcatalogue.core.catalogueelement.addrelation.AssetAddRelationService
import org.modelcatalogue.core.catalogueelement.addrelation.ValidationRuleAddRelationService
import org.modelcatalogue.core.catalogueelement.reorder.AbstractReorderInternalService
import org.modelcatalogue.core.catalogueelement.reorder.ValidationRuleReorderInternalService
import org.modelcatalogue.core.catalogueelement.searchwithinrelationships.AbstractSearchWithinRelationshipsService
import org.modelcatalogue.core.catalogueelement.searchwithinrelationships.ValidationRuleSearchWithinRelationshipsService
import org.modelcatalogue.core.persistence.ValidationRuleGormService
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.lists.Relationships

class ValidationRuleController extends AbstractCatalogueElementController<ValidationRule> {

    ValidationRuleGormService validationRuleGormService
    ValidationRuleReorderInternalService validationRuleReorderInternalService
    ValidationRuleAddRelationService validationRuleAddRelationService
    ValidationRuleSearchWithinRelationshipsService validationRuleSearchWithinRelationshipsService

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

    protected ValidationRule findById(long id) {
        validationRuleGormService.findById(id)
    }

    @Override
    protected AbstractReorderInternalService getReorderInternalService() {
        validationRuleReorderInternalService
    }

    @Override
    protected AbstractAddRelationService getAddRelationService() {
        validationRuleAddRelationService
    }

    @Override
    protected AbstractSearchWithinRelationshipsService getSearchWithinRelationshipsService() {
        validationRuleSearchWithinRelationshipsService
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
