package org.modelcatalogue.core

import grails.util.Environment
import org.hibernate.SessionFactory
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWrapper
import org.modelcatalogue.core.util.lists.Lists

class DataElementController extends AbstractCatalogueElementController<DataElement> {

    DataElementService dataElementService
    SessionFactory sessionFactory

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
        return isDatabaseFallback() && params.containsKey('tag')
    }


    @Override
    protected ListWrapper<DataElement> getAllEffectiveItems(Integer max) {
        if (isDatabaseFallback()) {
            return super.getAllEffectiveItems(max)
        }
        return Lists.wrap(params, "/${resourceName}/", dataElementService.findAllDataElementsInModel(params, DataModel.get(params.long('dataModel'))))
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


    protected boolean allowSaveAndEdit() {
        DataType dataType = DataType.get(getObjectToBind()?.dataType?.id)
        if(dataType && !modelCatalogueSecurityService.isSubscribed(dataType)) return false
        //if the user is a "general" curator they can create data models
//        if(resource == DataModel) return modelCatalogueSecurityService.hasRole('CURATOR')
        //but if they are trying to create something within the context of a data model they must have curator access to the specific model (not just general curator access)
        modelCatalogueSecurityService.hasRole('CURATOR', getDataModel())
    }

}
