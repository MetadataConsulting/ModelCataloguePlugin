package org.modelcatalogue.core.elasticsearch

import com.google.common.collect.ImmutableMap
import org.modelcatalogue.core.DataElement

class DataElementDocumentSerializer extends CatalogueElementDocumentSerializer<DataElement> {

    @Override
    ImmutableMap.Builder<String, Object> buildDocument(IndexingSession session, DataElement element, ImmutableMap.Builder<String, Object> builder) {
        super.buildDocument(session, element, builder)

        if (element.dataType) {
            safePut(builder, 'data_type', session.getDocument(element.dataType))
        }

        return builder
    }
}
