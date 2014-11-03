package org.modelcatalogue.core.util.marshalling

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
                mapping: map.mapping,
                elementType: Mapping.name
        ]
    }
}
