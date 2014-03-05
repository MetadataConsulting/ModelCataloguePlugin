package uk.co.mc.core.util.marshalling

import grails.converters.XML
import uk.co.mc.core.Relationship
import uk.co.mc.core.util.Relationships

class RelationshipsMarshaller extends ListWrapperMarshaller {

    RelationshipsMarshaller() {
        super(Relationships)
    }

    @Override
    protected Map<String, Object> prepareJsonMap(Object relationsList) {
        def ret = super.prepareJsonMap(relationsList)
        ret.list = relationsList.items.collect {
            def theOwner    = relationsList.direction == "sourceToDestination" ? it.source : it.destination
            def theRelation = relationsList.direction == "sourceToDestination" ? it.destination : it.source
            [id: it.id, type: it.relationshipType, relation: theRelation, direction: relationsList.direction, removeLink: getDeleteLink(theOwner, it)]
        }
        ret
    }

    @Override
    protected void buildItemsXml(Object relationsList, XML xml) {
        xml.build {
            for (Relationship rel in relationsList.items) {
                def theOwner    = relationsList.direction == "sourceToDestination" ? rel.source : rel.destination
                def theRelation = relationsList.direction == "sourceToDestination" ? rel.destination : rel.source
                relationship(id: rel.id, removeLink: getDeleteLink(theOwner, rel)) {
                    type rel.relationshipType
                    direction relationsList.direction
                    relation(theRelation)
                }
            }
        }
    }

    protected static getDeleteLink(theOwner, Relationship rel) {
        "${theOwner.info.link}/${theOwner == rel.source ? 'outgoing' : 'incoming'}/${rel.relationshipType.name}"
    }
}
