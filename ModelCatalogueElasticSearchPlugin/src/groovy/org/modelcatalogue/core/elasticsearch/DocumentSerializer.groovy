package org.modelcatalogue.core.elasticsearch

import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.security.User

import javax.management.relation.RelationType

interface DocumentSerializer<T> {

    Map getDocument(T object)

    class Registry {
        private static Map<Class, DocumentSerializer> documentSerializers = [
                (Asset)           : new AssetDocumentSerializer(),
                (CatalogueElement): new CatalogueElementDocumentSerializer(),
                (DataElement)     : new DataElementDocumentSerializer(),
                (DataType)        : new DataTypeDocumentSerializer(),
                (MeasurementUnit) : new MeasurementUnitDocumentSerializer(),
                (User)            : new UserDocumentSerializer(),
                (RelationType)    : new RelationshipTypeDocumentSerializer(),
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