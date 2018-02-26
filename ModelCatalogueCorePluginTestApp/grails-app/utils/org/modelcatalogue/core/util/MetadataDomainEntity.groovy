package org.modelcatalogue.core.util

import groovy.transform.CompileStatic

@CompileStatic
class MetadataDomainEntity {
    Long id
    MetadataDomain domain

    public static final String GORM_PREFFIX = 'gorm'
    public static final String DOMAIN_CLASS_PACKAGE = 'org.modelcatalogue.core'

    static MetadataDomainEntity of(String str) {

        if ( !str ) {
            return null
        }

        try {

            MetadataDomain metadataDomain
            String className

            if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.Asset:")) {
                className = 'Asset'
                metadataDomain =  MetadataDomain.ASSET

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.AssetFile:")) {
                className = 'AssetFile'
                metadataDomain =  MetadataDomain.ASSET_FILE

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.CatalogueElement:")) {
                className = 'CatalogueElement'
                metadataDomain =  MetadataDomain.CATALOGUE_ELEMENT

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.DataClass:")) {
                className = 'DataClass'
                metadataDomain =  MetadataDomain.DATA_CLASS

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.DataElement:")) {
                className = 'DataElement'
                metadataDomain =  MetadataDomain.DATA_ELEMENT

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.DataModel:")) {
                className = 'DataModel'
                metadataDomain =  MetadataDomain.DATA_MODEL

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.DataModelPolicy:")) {
                className = 'DataModelPolicy'
                metadataDomain =  MetadataDomain.DATA_MODEL_POLICY

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.DataType:")) {
                className = 'DataType'
                metadataDomain =  MetadataDomain.DATA_TYPE

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.EnumeratedType:")) {
                className = 'EnumeratedType'
                metadataDomain =  MetadataDomain.ENUMERATED_TYPE

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.ExtensionValue:")) {
                className = 'ExtensionValue'
                metadataDomain =  MetadataDomain.EXTENSION_VALUE

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.Mapping:")) {
                className = 'Mapping'
                metadataDomain =  MetadataDomain.MAPPING

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.MeasurementUnit:")) {
                className = 'MeasurementUnit'
                metadataDomain =  MetadataDomain.MEASUREMENT_UNIT

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.PrimitiveType:")) {
                className = 'PrimitiveType'
                metadataDomain =  MetadataDomain.PRIMITIVE_TYPE

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.ReferenceType:")) {
                className = 'ReferenceType'
                metadataDomain =  MetadataDomain.REFERENCE_TYPE

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.Relationship:")) {
                className = 'Relationship'
                metadataDomain =  MetadataDomain.RELATIONSHIP

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.RelationshipMetadata:")) {
                className = 'RelationshipMetadata'
                metadataDomain =  MetadataDomain.RELATIONSHIP_METADATA

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.RelationshipType:")) {
                className = 'RelationshipType'
                metadataDomain =  MetadataDomain.RELATIONSHIP_TYPE

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.RelationshipTag:")) {
                className = 'RelationshipTag'
                metadataDomain =  MetadataDomain.RELATIONSHIP_TAG

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.ValidationRule:")) {
                className = 'ValidationRule'
                metadataDomain =  MetadataDomain.BUSINESS_RULE

            } else if ( str.startsWith("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.Tag:")) {
                className = 'Tag'
                metadataDomain =  MetadataDomain.TAG

            }
            if ( className == null ||metadataDomain == null ) {
                return null
            }
            final Long id = Long.valueOf(str.substring("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.${className}:".length(), str.size()))
            return new MetadataDomainEntity(domain: metadataDomain, id: id)

        } catch( NumberFormatException e ) {
            return null
        }
    }

    static String stringRepresentation(MetadataDomain domain, Long id) {
        if ( domain == null || id == null ) {
            return null
        }
        String name = MetadataDomain.camelCaseDomainName(domain)
        "${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.${name}:$id".toString()
    }

    static String link(Long dataModelId, MetadataDomainEntity entity, String serverUrl) {
        String link = link(dataModelId, entity)
        if ( !link ) {
            return null
        }
        "${serverUrl}${link}".toString()
    }

    static String link(Long dataModelId, MetadataDomainEntity entity) {
        String name = MetadataDomain.lowerCamelCaseDomainName(entity.domain)

        if ( name ) {
            return link(dataModelId,name, entity.id)
        }
        null
    }

    static String link(Long dataModelId, String lowerCamelCaseDomain, Long domainId) {
        return "/#/${dataModelId}/${lowerCamelCaseDomain}/${domainId}"
    }
}
