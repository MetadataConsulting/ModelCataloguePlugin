package uk.co.mc.core.util.marshalling

import grails.converters.XML
import uk.co.mc.core.Relationship

/**
 * Created by ladin on 14.02.14.
 */
class RelationshipMarshallers extends AbstractMarshallers {

    RelationshipMarshallers() {
        super(Relationship)
    }

    protected Map<String, Object> prepareJsonMap(rel) {
        if (!rel) return [:]
        [
                id: rel.id,
                source: rel.source.info,
                destination: rel.destination.info,
                type: rel.relationshipType.info
        ]
    }

    protected void buildXml(rel, XML xml) {
        super.buildXml(rel, xml)
        xml.build {
            renderInfo('source', rel.source.info, xml)
            renderInfo('destination', rel.destination.info, xml)
            renderInfo('type', rel.relationshipType.info, xml)
        }
    }

    protected void addXmlAttributes(rel, XML xml) {
        super.addXmlAttributes(rel, xml)
        xml.attribute('id', "$rel.id")
    }

    private static void renderInfo(String what, Map info, XML xml) {
        xml.build {
            "$what" {
                for (e in info.entrySet()) {
                    if (e.key == 'id') {
                        xml.attribute('id', "$e.value")
                    } else {
                        "$e.key" e.value
                    }
                }
            }
        }
    }
}
