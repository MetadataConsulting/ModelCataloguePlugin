package uk.co.mc.core.util.marshalling

import grails.converters.XML
import uk.co.mc.core.Relationship
import uk.co.mc.core.util.Relationships

/**
 * Created by ladin on 19.02.14.
 */
class RelationshipsMarshaller extends AbstractMarshallers {

    RelationshipsMarshaller() {
        super(Relationships)
    }

    @Override
    protected Map<String, Object> prepareJsonMap(Object relationsList) {
        [
                success: true,
                total: relationsList.total,
                page: relationsList.page,
                offset: relationsList.offset,
                size: relationsList.relationships.size(),
                list: relationsList.relationships.collect {
                    [id: it.id, type: it.relationshipType, relation: (relationsList.direction == "sourceToDestination" ? it.destination : it.source), direction: relationsList.direction]
                },
                previous: relationsList.previous,
                next: relationsList.next,
        ]
    }

    @Override
    protected void buildXml(Object relationsList, XML xml) {
        xml.build {
            for (Relationship rel in relationsList.relationships) {
                relationship(id: rel.id) {
                    type rel.relationshipType
                    direction relationsList.direction
                    relation(relationsList.direction == "sourceToDestination" ? rel.destination : rel.source)
                }
            }
            previous relationsList.previous
            next relationsList.next
        }
    }

    @Override
    protected void addXmlAttributes(Object relationsList, XML xml) {
        xml.attribute("total", "${relationsList.total}")
        xml.attribute("offset", "${relationsList.offset}")
        xml.attribute("page", "${relationsList.page}")
        xml.attribute("size", "${relationsList.relationships.size()}")
        xml.attribute("success", "true")
    }
}
