package org.modelcatalogue.core.elasticsearch

import com.google.common.collect.ImmutableMap
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.security.User

interface DocumentSerializer<T> {

    ImmutableMap.Builder<String, Object> buildDocument(IndexingSession session, T object, ImmutableMap.Builder<String, Object> builder)

    class Registry {
        private static Map<Class, DocumentSerializer> documentSerializers = [
                (Asset)           : new AssetDocumentSerializer(),
                (CatalogueElement): new CatalogueElementDocumentSerializer(),
                (DataElement)     : new DataElementDocumentSerializer(),
                (DataType)        : new DataTypeDocumentSerializer(),
                (MeasurementUnit) : new MeasurementUnitDocumentSerializer(),
                (User)            : new UserDocumentSerializer(),
                (RelationshipType): new RelationshipTypeDocumentSerializer(),
                (Relationship)    : new RelationshipDocumentSerializer()
        ]

        static <T> void put(Class<T> type, DocumentSerializer<T> serializer) {
            documentSerializers[type] = serializer
        }

        static <T> DocumentSerializer<? super T> get(Class<T> clazz) {
            DocumentSerializer<T> serializer = documentSerializers[clazz]

            Class current = clazz
            while (serializer == null) {
                current = current.superclass
                serializer = documentSerializers[current]

                if (current == Object && !serializer) {
                    throw new IllegalArgumentException("Cannot find serializer for $clazz")
                }
            }

            return serializer
        }
    }

}