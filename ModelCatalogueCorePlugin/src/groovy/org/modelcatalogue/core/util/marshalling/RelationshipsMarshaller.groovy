package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.util.OrderedMap
import org.modelcatalogue.core.util.Relationships

class RelationshipsMarshaller extends ListWrapperMarshaller {

    RelationshipsMarshaller() {
        super(Relationships)
    }

    @Override
    protected Map<String, Object> prepareJsonMap(Object relationsList) {
        def ret = super.prepareJsonMap(relationsList)
        ret.type = relationsList.type
        ret.direction = relationsList.direction.actionName
        ret.list = relationsList.items.collect {
            [id: it.id, type: it.relationshipType, ext: OrderedMap.toJsonMap(it.ext), element: CatalogueElementMarshaller.minimalCatalogueElementJSON(relationsList.direction.getElement(relationsList.owner, it)),  relation: relationsList.direction.getRelation(relationsList.owner, it), direction: relationsList.direction.getDirection(relationsList.owner, it), removeLink: getDeleteLink(relationsList.owner, it), archived: it.archived, elementType: Relationship.name, classification: CatalogueElementMarshaller.minimalCatalogueElementJSON(it.classification)]
        }
        ret
    }

    static getDeleteLink(theOwner, Relationship rel) {
        "${theOwner.info.link}/${theOwner == rel.source ? 'outgoing' : 'incoming'}/${rel.relationshipType.name}"
    }
}
