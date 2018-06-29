package org.modelcatalogue.core.elasticsearch

import com.google.common.collect.ImmutableMap
import org.modelcatalogue.core.*
import org.modelcatalogue.core.enumeration.Enumerations

class DataTypeDocumentSerializer extends CatalogueElementDocumentSerializer<DataType> {

    @Override
    ImmutableMap.Builder<String, Object> buildDocument(IndexingSession session, DataType dataType, ImmutableMap.Builder<String, Object> builder) {
        super.buildDocument(session, dataType, builder)

        addDataTypeNestedObjects(dataType, builder, session)

        return builder
    }

    public static void addDataTypeNestedObjects(DataType dataType, ImmutableMap.Builder<String, Object> builder, IndexingSession session) {
        if (dataType.instanceOf(PrimitiveType) && dataType.measurementUnit) {
            safePut(builder, 'measurement_unit', session.getDocument(dataType.measurementUnit).payload)
        }

        if (dataType.instanceOf(ReferenceType) && dataType.dataClass) {
            safePut(builder, 'data_class', session.getDocument(dataType.dataClass).payload)
        }

        if (dataType.instanceOf(EnumeratedType) && dataType.enumerations) {
            Map<String, String> enums
            if (dataType.ext) {
                enums = new TreeMap<>()
                enums.putAll(dataType.ext)
                ((EnumeratedType)dataType).getEnumerations().each {enums.put(it.key, it.value)}
            } else { // just need this when nested enumerated_value problem solved
                enums = ((EnumeratedType)dataType).getEnumerations()
            }
            safePut(builder, 'ext', getExtensions(enums)) // change to 'enumerated_value' as key when nested enumerated_value problem solved
        }
    }
}
