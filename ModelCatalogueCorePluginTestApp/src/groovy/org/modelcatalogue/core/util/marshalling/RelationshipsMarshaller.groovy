package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.security.User
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

    protected List getList(Object relationsList){
        List list = []
        relationsList.items.each{ item ->
            CatalogueElement relation = relationsList.direction.getRelation(relationsList.owner, item)
            if(isSubscribed(relation)){
                list.add([id: item.id, type: item.relationshipType, ext: OrderedMap.toJsonMap(item.ext), element: CatalogueElementMarshaller.minimalCatalogueElementJSON(relationsList.direction.getElement(relationsList.owner, item)),  relation: relationsList.direction.getRelation(relationsList.owner, item), direction: relationsList.direction.getDirection(relationsList.owner, item), removeLink: getDeleteLink(relationsList.owner, item), archived: item.archived, inherited: item.inherited, elementType: Relationship.name, classification: CatalogueElementMarshaller.minimalCatalogueElementJSON(item.dataModel)])
            }else{
                // TODO : FIX THE AUTHENTICATED BIT  - CREATE A SPECIAL CLASS THAT IS AN UNAUTHENTICATED ITEM ->need to render it in the ui

                list.add([id: item.id, type: item.relationshipType, element: CatalogueElementMarshaller.minimalCatalogueElementJSON(relationsList.direction.getElement(relationsList.owner, item)), relation: new DataElement(name: "not authenticated"), direction: relationsList.direction.getDirection(relationsList.owner, item), removeLink: getDeleteLink(relationsList.owner, item), archived: item.archived, inherited: item.inherited, elementType: Relationship.name, classification: CatalogueElementMarshaller.minimalCatalogueElementJSON(item.dataModel)])
            }
        }
        list
    }


    //TODO move code to seperate service (this is replicated in the abstract restful controller



    protected Boolean isSubscribed(CatalogueElement ce){
        Set subscriptions = new HashSet<>()
        subscriptions = ce?.dataModel?.subscriptions
        if(subscriptions && subscriptions.contains(modelCatalogueSecurityService.getCurrentUser())){
            return true
        }
        false
    }
}
