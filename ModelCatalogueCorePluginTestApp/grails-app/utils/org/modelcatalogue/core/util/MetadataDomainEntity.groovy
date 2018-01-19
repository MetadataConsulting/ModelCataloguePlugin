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

        String gormPreffix = 'gorm'
        String domainClassesPackage = 'org.modelcatalogue.core'

        try {

            if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.Asset:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.Asset:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.ASSET, id: id)

            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.AssetFile:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.AssetFile:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.ASSET_FILE, id: id)
            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.CatalogueElement:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.CatalogueElement:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.CATALOGUE_ELEMENT, id: id)
            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.DataClass:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.DataClass:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.DATA_CLASS, id: id)
            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.DataElement:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.DataElement:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.DATA_ELEMENT, id: id)
            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.DataModel:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.DataModel:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.DATA_MODEL, id: id)
            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.DataModelPolicy:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.DataModelPolicy:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.DATA_MODEL_POLICY, id: id)
            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.DataType:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.DataType:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.DATA_TYPE, id: id)
            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.EnumeratedType:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.EnumeratedType:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.ENUMERATED_TYPE, id: id)
            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.ExtensionValue:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.ExtensionValue:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.EXTENSION_VALUE, id: id)
            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.Mapping:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.Mapping:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.MAPPING, id: id)
            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.MeasurementUnit:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.MeasurementUnit:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.MEASUREMENT_UNIT, id: id)
            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.PrimitiveType:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.PrimitiveType:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.PRIMITIVE_TYPE, id: id)
            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.ReferenceType:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.ReferenceType:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.REFERENCE_TYPE, id: id)
            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.Relationship:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.Relationship:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.RELATIONSHIP, id: id)
            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.RelationshipMetadata:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.RelationshipMetadata:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.RELATIONSHIP_METADATA, id: id)
            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.RelationshipType:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.RelationshipType:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.RELATIONSHIP_TYPE, id: id)
            } else if ( str.startsWith("${gormPreffix}://${domainClassesPackage}.RelationshipTag:")) {
                Long id = Long.valueOf(str.substring("${gormPreffix}://${domainClassesPackage}.RelationshipTag:".length(), str.size()))
                return new MetadataDomainEntity(domain: MetadataDomain.RELATIONSHIP_TAG, id: id)
            }

        } catch( NumberFormatException e ) {
            return null
        }

        null
    }
}
