package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import org.modelcatalogue.core.Relationship

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
                source: CatalogueElementMarshallers.minimalCatalogueElementJSON(rel.source),
                destination: CatalogueElementMarshallers.minimalCatalogueElementJSON(rel.destination),
                type: rel.relationshipType.info,
                archived: rel.archived,
                ext: rel.ext
        ]
    }

    protected void buildXml(rel, XML xml) {
        super.buildXml(rel, xml)
        xml.build {
            renderInfo('source', rel.source.info, xml)
            renderInfo('destination', rel.destination.info, xml)
            renderInfo('type', rel.relationshipType.info, xml)
        }
        if (rel.ext) {
            xml.build {
                extensions {
                    for (e in rel.ext.entrySet()) {
                        extension key: e.key, e.value
                    }
                }
            }
        }
    }

    protected void addXmlAttributes(rel, XML xml) {
        super.addXmlAttributes(rel, xml)
        addXmlAttribute(rel.id, "id", xml)
        addXmlAttribute(rel.archived, "archived", xml)
    }

    static void renderInfo(String what, Map info, XML xml) {
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
