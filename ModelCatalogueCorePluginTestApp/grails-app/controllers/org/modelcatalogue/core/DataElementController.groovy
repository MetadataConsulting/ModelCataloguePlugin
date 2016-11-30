package org.modelcatalogue.core

import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists

class DataElementController extends AbstractCatalogueElementController<DataElement> {

    DataElementController() {
        super(DataElement, false)
    }

    def content() {
        DataElement dataElement = DataElement.get(params.id)
        if (!dataElement) {
            notFound()
            return
        }


        ListWithTotalAndType<Map> list = Lists.lazy(params, Map) {
            if (dataElement.dataType) {
                return [dataElement.dataType]
            }
            return []
        }

        respond Lists.wrap(params, "/${resourceName}/${params.id}/content", list)
    }

    @Override
    protected boolean hasAdditionalIndexCriteria() {
        return params.containsKey('tag')
    }

    @Override
    protected Closure buildAdditionalIndexCriteria() {
        if (!hasAdditionalIndexCriteria()) {
            return super.buildAdditionalIndexCriteria()
        }

        Long tagID = params.long('tag')

        Tag tag = Tag.get(tagID)

        if (!tag) {
            return super.buildAdditionalIndexCriteria()
        }

        return {
            incomingRelationships {
                eq 'source', tag
                eq 'relationshipType', RelationshipType.tagType
            }
        }
    }
}
