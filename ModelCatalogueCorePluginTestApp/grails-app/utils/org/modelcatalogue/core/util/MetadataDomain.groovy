package org.modelcatalogue.core.util

import groovy.transform.CompileStatic
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.AssetFile
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelPolicy
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipMetadata
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.Tag
import org.modelcatalogue.core.ValidationRule

@CompileStatic
enum MetadataDomain {
    ASSET('asset', Asset),
    ASSET_FILE('assetFile', AssetFile),
    CATALOGUE_ELEMENT('catalogueElement', CatalogueElement),
    DATA_CLASS('dataClass', DataClass),
    DATA_ELEMENT('dataElement', DataElement),
    DATA_MODEL('dataModel', DataModel),
    DATA_MODEL_POLICY('dataModelPolicy', DataModelPolicy),
    DATA_TYPE('dataType', DataType),
    ENUMERATED_TYPE('enumeratedType', EnumeratedType),
    EXTENSION_VALUE('extensionValue', ExtensionValue),
    MAPPING('mapping', Mapping),
    MEASUREMENT_UNIT('measurementUnit', MeasurementUnit),
    PRIMITIVE_TYPE('primitiveType', PrimitiveType),
    REFERENCE_TYPE('referenceType', ReferenceType),
    RELATIONSHIP('relationship', Relationship),
    RELATIONSHIP_METADATA('relationshipMetadata', RelationshipMetadata),
    RELATIONSHIP_TYPE('relationshipType', RelationshipType),
    RELATIONSHIP_TAG('relationshipTag', null),
    BUSINESS_RULE('validationRule', ValidationRule),
    TAG('tag', Tag)

    private final String lowerCamelCaseDomainName
    private final Class objectClass

    MetadataDomain(String lowerCamelCaseDomainName, Class objectClass) {
        this.lowerCamelCaseDomainName = lowerCamelCaseDomainName
        this.objectClass = objectClass
    }

    MetadataDomain(String lowerCamelCaseDomainName) {
        this.lowerCamelCaseDomainName = lowerCamelCaseDomainName
    }

    static MetadataDomain ofClass(Class clazz) {

        for (MetadataDomain metadataDomain: values()) {
            if (clazz == metadataDomain.objectClass) {
                return metadataDomain
                break
            }
        }
        return CATALOGUE_ELEMENT
    }
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

        } else if ( catalogueElement instanceof ValidationRule ) {
            return BUSINESS_RULE

        } else if ( catalogueElement instanceof ValidationRule ) {
            return TAG
        }
        CATALOGUE_ELEMENT
    }

    static String lowerCamelCaseDomainName(MetadataDomain domain) {
        return domain?.lowerCamelCaseDomainName
    }

    static String camelCaseDomainName(MetadataDomain domain) {
        return domain?.lowerCamelCaseDomainName?.capitalize()
    }
}
