package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.util.Mappings

class MappingsMarshaller extends ListWrapperMarshaller {

    MappingsMarshaller() {
        super(Mappings)
    }

    @Override
    protected Map<String, Object> prepareJsonMap(Object mappings) {
        def ret = super.prepareJsonMap(mappings)
        ret.list = mappings.items.collect {
            [id: it.id, source: CatalogueElementMarshaller.minimalCatalogueElementJSON(it.source),  mapping: it.mapping, destination: it.destination, removeLink: "${it.source.info.link}/mapping/${it.destination.id}", elementType: Mapping.name]
        }
        ret
    }
}
