package org.modelcatalogue.core

import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.lists.Relationships

class TagController extends AbstractCatalogueElementController<Tag> {

    TagController() {
        super(Tag, false)
    }

    @Override
    protected boolean hasUniqueName() {
        true
    }

    def content(Integer max) {
        handleParams(max)

        params.sort = 'outgoingIndex'

        Tag element = queryForResource(params.id)

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
                eq 'relationshipType', RelationshipType.tagType

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

                sort('outgoingIndex')
            }
        )
    }

}
