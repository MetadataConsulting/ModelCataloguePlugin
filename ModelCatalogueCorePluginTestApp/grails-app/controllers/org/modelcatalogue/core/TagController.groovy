package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.hibernate.SessionFactory
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.OrderedMap
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.lists.Relationships
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller

class TagController extends AbstractCatalogueElementController<Tag> {

    SessionFactory sessionFactory

    TagController() {
        super(Tag, false)
    }

    @Override
    protected boolean hasUniqueName() {
        true
    }


    def forDataModel() {
        final Long dataModelId = params.long('dataModelId')

        if (!dataModelId) {
            notFound()
            return
        }

        final DataModel dataModel = dataModelGormService.get(dataModelId)

        if (!dataModel) {
            notFound()
            return
        }

        respond Lists.wrap(params, "/${resourceName}/forDataModel/${dataModelId}", Lists.lazy([:], Map, {
            DataModel model = dataModelGormService.get(dataModelId)
            List<Tag> tags =  sessionFactory.currentSession.connection().metaData.databaseProductName != 'MySQL' ? DataModelService.allTags(model) : dataModelService.allTagsMySQL(model)

            List<Map<String, Object>> ret = [createAllDataElementsDescriptor(model), createUntaggedDescriptor(model)]

            for (tag in tags) {
                ret << createTagContentDescriptor(tag, model)
            }

            return ret
        }))
    }

    private static Map<String, Object> createUntaggedDescriptor(DataModel dataModel) {
        String link = "/dataElement?tag=none&dataModel=${dataModel.getId()}&status=${dataModel.status != ElementStatus.DEPRECATED ? 'active' : ''}"
        Map<String, Object> ret = [:]
        ret.id = 'notags'
        ret.dataModels = [CatalogueElementMarshaller.minimalCatalogueElementJSON(dataModel)]
        ret.elementType = Tag.name
        ret.name = 'No tags'
        ret.content = [count: Integer.MAX_VALUE, itemType: DataElement.name, link: link]
        ret.link = link
        ret.resource = GrailsNameUtils.getPropertyName(Tag)
        ret.status = dataModel.status.toString()
        ret.tagId = 'none'
        ret
    }

    private static Map<String, Object> createAllDataElementsDescriptor(DataModel dataModel) {
        String link = "/dataElement?deep=true&dataModel=${dataModel.getId()}&status=${dataModel.status != ElementStatus.DEPRECATED ? 'active' : ''}"
        Map<String, Object> ret = [:]
        ret.id = 'all'
        ret.dataModels = [CatalogueElementMarshaller.minimalCatalogueElementJSON(dataModel)]
        ret.elementType = Tag.name
        ret.name = 'All (including imports)'
        ret.content = [count: Integer.MAX_VALUE, itemType: DataElement.name, link: link]
        ret.link = link
        ret.resource = GrailsNameUtils.getPropertyName(Tag)
        ret.status = dataModel.status.toString()
        ret.tagId = 'all'
        ret
    }

    private static Map<String, Object> createTagContentDescriptor(CatalogueElement tag, DataModel dataModel) {
        String link = "/dataElement?tag=${tag.getId()}&dataModel=${dataModel.getId()}&status=${dataModel.status != ElementStatus.DEPRECATED ? 'active' : ''}"
        Map<String, Object> ret = [:]
        ret.id = 'tag-' + tag.id
        ret.dataModels = [CatalogueElementMarshaller.minimalCatalogueElementJSON(dataModel)]
        ret.elementType = Tag.name
        ret.name = tag?.name ?: 'No Tag'
        ret.content = [count: Integer.MAX_VALUE, itemType: DataElement.name, link: link]
        ret.link = link
        ret.resource = GrailsNameUtils.getPropertyName(Tag)
        ret.status = dataModel.status.toString()
        ret.tagId = tag.id
        ret
    }

}
