package org.modelcatalogue.core.util

import groovy.transform.CompileStatic
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType

@CompileStatic
enum MetadataDomain {
    ASSET,
    ASSET_FILE,
    CATALOGUE_ELEMENT,
    DATA_CLASS,
    DATA_ELEMENT,
    DATA_MODEL,
    DATA_MODEL_POLICY,
    DATA_TYPE,
    ENUMERATED_TYPE,
    EXTENSION_VALUE,
    MAPPING,
    MEASUREMENT_UNIT,
    PRIMITIVE_TYPE,
    REFERENCE_TYPE,
    RELATIONSHIP,
    RELATIONSHIP_METADATA,
    RELATIONSHIP_TYPE,
    RELATIONSHIP_TAG,


    static MetadataDomain of(CatalogueElement catalogueElement) {

        if ( catalogueElement instanceof Asset ) {
            return ASSET

        } else if ( catalogueElement instanceof DataClass ) {
            return DATA_CLASS

        } else if ( catalogueElement instanceof DataElement ) {
            return DATA_ELEMENT

        } else if ( catalogueElement instanceof DataModel ) {
            return DATA_MODEL

        } else if ( catalogueElement instanceof EnumeratedType ) {
            return ENUMERATED_TYPE

        } else if ( catalogueElement instanceof MeasurementUnit ) {
            return MEASUREMENT_UNIT

        } else if ( catalogueElement instanceof PrimitiveType ) {
            return PRIMITIVE_TYPE

        } else if ( catalogueElement instanceof ReferenceType ) {
            return REFERENCE_TYPE

        } else if ( catalogueElement instanceof DataType ) {
            return DATA_TYPE
        }
        CATALOGUE_ELEMENT
    }

    static String lowerCamelCaseDomainName(MetadataDomain domain) {
        switch (domain) {
            case ASSET:
                return 'asset'
            case ASSET_FILE:
                return 'assetFile'
            case CATALOGUE_ELEMENT:
                return 'catalogueElement'
            case DATA_CLASS:
                return 'dataClass'
            case DATA_ELEMENT:
                return 'dataElement'
            case DATA_MODEL:
                return 'dataModel'
            case DATA_MODEL_POLICY:
                return 'dataModelPolicy'
            case DATA_TYPE:
                return 'dataType'
            case ENUMERATED_TYPE:
                return 'enumeratedType'
            case EXTENSION_VALUE:
                return 'extensionValue'
            case MAPPING:
                return 'mapping'
            case MEASUREMENT_UNIT:
                return 'measurementUnit'
            case PRIMITIVE_TYPE:
                return 'primitiveType'
            case REFERENCE_TYPE:
                return 'referenceType'
            case RELATIONSHIP:
                return 'relationship'
            case RELATIONSHIP_METADATA:
                return 'relationshipMetadata'
            case RELATIONSHIP_TYPE:
                return 'relationshipType'
            case RELATIONSHIP_TAG:
                return 'relationshipTag'
            default:
                return null
        }
    }

    static String camelCaseDomainName(MetadataDomain domain) {
        switch (domain) {
            case ASSET:
                return 'Asset'
            case ASSET_FILE:
                return 'AssetFile'
            case CATALOGUE_ELEMENT:
                return 'CatalogueElement'
            case DATA_CLASS:
                return 'DataClass'
            case DATA_ELEMENT:
                return 'DataElement'
            case DATA_MODEL:
                return 'DataModel'
            case DATA_MODEL_POLICY:
                return 'DataModelPolicy'
            case DATA_TYPE:
                return 'DataType'
            case ENUMERATED_TYPE:
                return 'EnumeratedType'
            case EXTENSION_VALUE:
                return 'ExtensionValue'
            case MAPPING:
                return 'Mapping'
            case MEASUREMENT_UNIT:
                return 'MeasurementUnit'
            case PRIMITIVE_TYPE:
                return 'PrimitiveType'
            case REFERENCE_TYPE:
                return 'ReferenceType'
            case RELATIONSHIP:
                return 'Relationship'
            case RELATIONSHIP_METADATA:
                return 'RelationshipMetadata'
            case RELATIONSHIP_TYPE:
                return 'RelationshipType'
            case RELATIONSHIP_TAG:
                return 'RelationshipTag'
            default:
                return null
        }
    }
}
