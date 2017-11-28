package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.util.OrderedMap
import org.modelcatalogue.core.util.lists.Relationships

class RelationshipsMarshaller extends ListWrapperMarshaller {

    RelationshipsMarshaller() {
        super(Relationships)
    }

    @Override
    protected Map<String, Object> prepareJsonMap(Object relationsList) {
        def ret = super.prepareJsonMap(relationsList)
        ret.type = relationsList.type
        ret.direction = relationsList.direction.actionName
        ret.list = getList(relationsList)
        ret.total -= relationsList.items.size() - relationsList.items.grep().size()
        ret
    }


    protected getSize(Object elements) {
        elements.items.grep().size()
    }

    static getDeleteLink(theOwner, Relationship rel) {
        "${theOwner.info.link}/${theOwner == rel.source ? 'outgoing' : 'incoming'}/${rel.relationshipType.name}"
    }

    protected List getList(Object relationsList) {
        List list = []
        relationsList.items.each { item ->
            if (item) {

                CatalogueElement relation = relationsList.direction.getRelation(relationsList.owner, item)
                boolean hasReadPermission = dataModelAclService.hasReadPermission(relation)
                if ( hasReadPermission ) {
                    list.add([id            : item.id,
                              type          : item.relationshipType,
                              ext           : OrderedMap.toJsonMap(item.ext),
                              element       : CatalogueElementMarshaller.minimalCatalogueElementJSON(relationsList.direction.getElement(relationsList.owner, item)),
                              relation      : relation,
                              direction     : relationsList.direction.getDirection(relationsList.owner, item),
                              removeLink    : getDeleteLink(relationsList.owner, item),
                              archived      : item.archived,
                              inherited     : item.inherited,
                              elementType   : Relationship.name,
                              classification: CatalogueElementMarshaller.minimalCatalogueElementJSON(item.dataModel)])
                } else {
                    list.add([id            : item.id,
                              type          : item.relationshipType,
                              ext           : OrderedMap.toJsonMap([:]),
                              element       : CatalogueElementMarshaller.minimalCatalogueElementJSON(relationsList.direction.getElement(relationsList.owner, item)),
                              relation      : getUnAuthenticatedElement(relation),
                              direction     : relationsList.direction.getDirection(relationsList.owner, item),
                              removeLink    : getDeleteLink(relationsList.owner, item),
                              archived      : item.archived,
                              inherited     : item.inherited,
                              elementType   : Relationship.name,
                              classification: CatalogueElementMarshaller.minimalCatalogueElementJSON(item.dataModel)])
                }
            }
        }
        list
    }

    protected Map<String, String> getUnAuthenticatedElement(CatalogueElement element) {
        [
                dateCreated             : "",
                versionCreated          : "",
                lastUpdated             : "",
                internalModelCatalogueId: element.defaultModelCatalogueId,
                modelCatalogueId        : element.modelCatalogueId,
                name                    : "$element.name (Not authorised to view details)",
                classifiedName          : "Not authorised to view details",
                id                      : element.id,
                description             : "Not authorised to view element details. Please contact your system administrator to give you access to model ${element?.dataModel?.name} (${element?.dataModel?.semanticVersion}).",
                elementType             : element.getClass().name,
                link                    : "/${CatalogueElement.fixResourceName(GrailsNameUtils.getPropertyName(element.getClass()))}/$element.id".toString(),
                status                  : "${element.status}".toString(), versionNumber: "", latestVersionId: element.latestVersionId ?: element.id,
                dataType                : ""
        ]
    }
}
