package org.modelcatalogue.core

import org.hibernate.SessionFactory
import org.modelcatalogue.core.persistence.DataElementGormService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWrapper
import org.modelcatalogue.core.util.lists.Lists

class DataElementController extends AbstractCatalogueElementController<DataElement> {

    DataElementService dataElementService
    SessionFactory sessionFactory
    DataModelGormService dataModelGormService
    DataElementGormService dataElementGormService

    DataElementController() {
        super(DataElement, false)
    }

    def content() {
        long dataElementId = params.long('id')
        DataElement dataElement = dataElementGormService.findById(dataElementId)
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

        respond Lists.wrap(params, "/${resourceName}/${dataElementId}/content", list)
    }

    @Override
    protected boolean hasAdditionalIndexCriteria() {
        return isDatabaseFallback() && params.containsKey('tag')
    }


    @Override
    protected ListWrapper<DataElement> getAllEffectiveItems(Integer max) {
        if (isDatabaseFallback()) {
            return super.getAllEffectiveItems(max)
        }
        return Lists.wrap(params, "/${resourceName}/", dataElementService.findAllDataElementsInModel(params, dataModelGormService.findById(params.long('dataModel'))))
    }

    private boolean isDatabaseFallback() {
        !params.long("dataModel") || sessionFactory.currentSession.connection().metaData.databaseProductName != 'MySQL'
    }

    @Override
    protected Closure buildAdditionalIndexCriteria() {
        if (!hasAdditionalIndexCriteria()) {
            return super.buildAdditionalIndexCriteria()
        }

        if (params.tag in ['none', 'null', 'undefined']) {
            // TODO: this is far to be optimal solution
            return {
                not {
                  inList 'id', Relationship.where { relationshipType == RelationshipType.tagType }.distinct('destination').list()*.id
                }
            }
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

    protected DataElement findById(long id) {
        dataElementGormService.findById(id)
    }
}
