package org.modelcatalogue.core

import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.lists.Relationships


class ValidationRuleController extends AbstractCatalogueElementController<ValidationRule> {

    ValidationRuleController() {
        super(ValidationRule, false)
    }

    def content(Integer max) {
        handleParams(max)

        params.sort = 'incomingIndex'

        ValidationRule element = queryForResource(params.id)

        if (!element) {
            notFound()
            return
        }

        DataModelFilter filter = overridableDataModelFilter

        respond new Relationships(
            owner: element,
            direction: RelationshipDirection.INCOMING,
            list: Lists.fromCriteria(params, Relationship, "/${resourceName}/${params.id}/content") {
                join 'source'
                eq 'destination', element
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

}
