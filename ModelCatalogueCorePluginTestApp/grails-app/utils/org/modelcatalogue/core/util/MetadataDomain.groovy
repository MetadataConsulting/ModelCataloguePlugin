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
            return MetadataDomain.ASSET

        } else if ( catalogueElement instanceof DataClass ) {
            return MetadataDomain.DATA_CLASS

        } else if ( catalogueElement instanceof DataElement ) {
            return MetadataDomain.DATA_ELEMENT

        } else if ( catalogueElement instanceof DataModel ) {
            return MetadataDomain.DATA_MODEL

        } else if ( catalogueElement instanceof EnumeratedType ) {
            return MetadataDomain.ENUMERATED_TYPE

        } else if ( catalogueElement instanceof MeasurementUnit ) {
            return MetadataDomain.MEASUREMENT_UNIT

        } else if ( catalogueElement instanceof PrimitiveType ) {
            return MetadataDomain.PRIMITIVE_TYPE

        } else if ( catalogueElement instanceof ReferenceType ) {
            return MetadataDomain.REFERENCE_TYPE

        } else if ( catalogueElement instanceof DataType ) {
            return MetadataDomain.DATA_TYPE
        }
        MetadataDomain.CATALOGUE_ELEMENT
    }
}
