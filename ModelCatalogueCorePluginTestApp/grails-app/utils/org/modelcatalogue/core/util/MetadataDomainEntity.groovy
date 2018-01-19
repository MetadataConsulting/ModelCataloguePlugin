package org.modelcatalogue.core.util

import groovy.transform.CompileStatic

@CompileStatic
class MetadataDomainEntity {
    Long id
    MetadataDomain domain

    static MetadataDomainEntity of(String str) {

        if ( !str ) {
            return null
        }

        final String gormPreffix = 'gorm'
        final String domainClassesPackage = 'org.modelcatalogue.core'

        try {

            MetadataDomain metadataDomain
            String className

            if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.Asset:")) {
                className = 'Asset'
                metadataDomain =  MetadataDomain.ASSET

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.AssetFile:")) {
                className = 'AssetFile'
                metadataDomain =  MetadataDomain.ASSET_FILE

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.CatalogueElement:")) {
                className = 'CatalogueElement'
                metadataDomain =  MetadataDomain.CATALOGUE_ELEMENT

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.DataClass:")) {
                className = 'DataClass'
                metadataDomain =  MetadataDomain.DATA_CLASS

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.DataElement:")) {
                className = 'DataElement'
                metadataDomain =  MetadataDomain.DATA_ELEMENT

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.DataModel:")) {
                className = 'DataModel'
                metadataDomain =  MetadataDomain.DATA_MODEL

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.DataModelPolicy:")) {
                className = 'DataModelPolicy'
                metadataDomain =  MetadataDomain.DATA_MODEL_POLICY

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.DataType:")) {
                className = 'DataType'
                metadataDomain =  MetadataDomain.DATA_TYPE

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.EnumeratedType:")) {
                className = 'EnumeratedType'
                metadataDomain =  MetadataDomain.ENUMERATED_TYPE

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.ExtensionValue:")) {
                className = 'ExtensionValue'
                metadataDomain =  MetadataDomain.EXTENSION_VALUE

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.Mapping:")) {
                className = 'Mapping'
                metadataDomain =  MetadataDomain.MAPPING

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.MeasurementUnit:")) {
                className = 'MeasurementUnit'
                metadataDomain =  MetadataDomain.MEASUREMENT_UNIT

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.PrimitiveType:")) {
                className = 'PrimitiveType'
                metadataDomain =  MetadataDomain.PRIMITIVE_TYPE

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.ReferenceType:")) {
                className = 'ReferenceType'
                metadataDomain =  MetadataDomain.REFERENCE_TYPE

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.Relationship:")) {
                className = 'Relationship'
                metadataDomain =  MetadataDomain.RELATIONSHIP

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.RelationshipMetadata:")) {
                className = 'RelationshipMetadata'
                metadataDomain =  MetadataDomain.RELATIONSHIP_METADATA

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.RelationshipType:")) {
                className = 'RelationshipType'
                metadataDomain =  MetadataDomain.RELATIONSHIP_TYPE

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.RelationshipTag:")) {
                className = 'RelationshipTag'
                metadataDomain =  MetadataDomain.RELATIONSHIP_TAG

            }
            if ( className == null ||metadataDomain == null ) {
                return null
            }
            final Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.${className}:".length(), str.size()))
            return new MetadataDomainEntity(domain: metadataDomain, id: id)

        } catch( NumberFormatException e ) {
            return null
        }
    }

    static String link(Long dataModelId, MetadataDomainEntity entity) {
        String name = null
        switch (entity.domain) {
            case MetadataDomain.ASSET:
                name = 'asset'
                break
            case MetadataDomain.ASSET_FILE:
                name = 'assetFile'
                break
            case MetadataDomain.CATALOGUE_ELEMENT:
                name = 'catalogueElement'
                break
            case MetadataDomain.DATA_CLASS:
                name = 'dataClass'
                break
            case MetadataDomain.DATA_ELEMENT:
                name = 'dataElement'
                break
            case MetadataDomain.DATA_MODEL:
                name = 'dataModel'
                break
            case MetadataDomain.DATA_MODEL_POLICY:
                name = 'dataPolicy'
                break
            case MetadataDomain.DATA_TYPE:
                name = 'dataType'
                break
            case MetadataDomain.ENUMERATED_TYPE:
                name = 'enumeratedType'
                break
            case MetadataDomain.EXTENSION_VALUE:
                name = 'extensionValue'
                break
            case MetadataDomain.MAPPING:
                name = 'mapping'
                break
            case MetadataDomain.MEASUREMENT_UNIT:
                name = 'measurmentUnit'
                break
            case MetadataDomain.PRIMITIVE_TYPE:
                name = 'primitiveType'
                break
            case MetadataDomain.REFERENCE_TYPE:
                name = 'referenceType'
                break
            case MetadataDomain.RELATIONSHIP:
                name = 'relationship'
                break
            case MetadataDomain.RELATIONSHIP_METADATA:
                name = 'relationshipMetadata'
                break
            case MetadataDomain.RELATIONSHIP_TYPE:
                name = 'relationshipType'
                break
            case MetadataDomain.RELATIONSHIP_TAG:
                name = 'relationshipTag'
                break
            default:
                return null
        }
        if ( name ) {
            return link(dataModelId,name, entity.id)
        }
        null
    }

    static String link(Long dataModelId, String lowerCamelCaseDomain, Long domainId) {
        return "/#/${dataModelId}/${lowerCamelCaseDomain}/${domainId}"
    }
}
