package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import org.modelcatalogue.core.Mapping

/**
 * Created by ladin on 14.02.14.
 */
class MappingMarshallers extends AbstractMarshallers {

    MappingMarshallers() {
        super(Mapping)
    }

    protected Map<String, Object> prepareJsonMap(map) {
        if (!map) return [:]
        [
                id: map.id,
                source: CatalogueElementMarshallers.minimalCatalogueElementJSON(map.source),
                destination: CatalogueElementMarshallers.minimalCatalogueElementJSON(map.destination),
                mapping: map.mapping
        ]
    }

    protected void buildXml(map, XML xml) {
        super.buildXml(map, xml)
        xml.build {
            RelationshipMarshallers.renderInfo('source', map.source.info, xml)
            RelationshipMarshallers.renderInfo('destination', map.destination.info, xml)
            mapping map.mapping
        }
    }

    protected void addXmlAttributes(map, XML xml) {
        super.addXmlAttributes(map, xml)
        addXmlAttribute(map.id, "id", xml)
    }
}
