package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.Mapping

/**
 * Created by ladin on 14.02.14.
 */
class MappingMarshaller extends AbstractMarshaller {

    MappingMarshaller() {
        super(Mapping)
    }

    protected Map<String, Object> prepareJsonMap(map) {
        if (!map) return [:]
        [
                id: map.id,
                source: CatalogueElementMarshaller.minimalCatalogueElementJSON(map.source),
                destination: CatalogueElementMarshaller.minimalCatalogueElementJSON(map.destination),
                mapping: map.mapping,
                elementType: Mapping.name
        ]
    }
}
