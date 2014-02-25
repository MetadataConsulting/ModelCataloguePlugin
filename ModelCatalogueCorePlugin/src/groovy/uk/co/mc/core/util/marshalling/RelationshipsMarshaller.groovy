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
                    [id: it.id, type: it.relationshipType, relation: (relationsList.direction == "sourceToDestination" ? it.destination : it.source), direction: relationsList.direction]
        }
        ret
    }

    @Override
    protected void buildItemsXml(Object relationsList, XML xml) {
        xml.build {
            for (Relationship rel in relationsList.items) {
                relationship(id: rel.id) {
                    type rel.relationshipType
                    direction relationsList.direction
                    relation(relationsList.direction == "sourceToDestination" ? rel.destination : rel.source)
                }
            }
        }
    }
}
