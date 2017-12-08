package org.modelcatalogue.core.elasticsearch

import com.google.common.collect.ImmutableMap
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType

class DataElementDocumentSerializer extends CatalogueElementDocumentSerializer<DataElement> {

    @Override
    ImmutableMap.Builder<String, Object> buildDocument(IndexingSession session, DataElement element, ImmutableMap.Builder<String, Object> builder) {
        super.buildDocument(session, element, builder)

        if (element.getDataTypeId()) {
            DataType.withNewSession {
                safePut(builder, 'data_type', session.getDocument(DataType.get(element.getDataTypeId())).payload)
            }
        }
        return builder
    }
}
