package org.modelcatalogue.core.elasticsearch

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import org.modelcatalogue.core.*

class DataTypeDocumentSerializer extends CatalogueElementDocumentSerializer<DataType> {

    @Override
    ImmutableMap.Builder<String, Object> buildDocument(IndexingSession session, DataType dataType, ImmutableMap.Builder<String, Object> builder) {
        super.buildDocument(session, dataType, builder)

        addDataTypeNestedObjects(dataType, builder, session)

        return builder
    }

    public static void addDataTypeNestedObjects(DataType dataType, ImmutableMap.Builder<String, Object> builder, IndexingSession session) {
        if (dataType.instanceOf(PrimitiveType) && dataType.measurementUnit) {
            safePut(builder, 'measurement_unit', session.getDocument(dataType.measurementUnit))
        }

        if (dataType.instanceOf(ReferenceType) && dataType.dataClass) {
            safePut(builder, 'data_class', session.getDocument(dataType.dataClass))
        }

        if (dataType.instanceOf(EnumeratedType) && dataType.enumerations) {
            safePut(builder, 'enumerated_value', getExtensions(dataType.enumerations))
        }
    }
}
