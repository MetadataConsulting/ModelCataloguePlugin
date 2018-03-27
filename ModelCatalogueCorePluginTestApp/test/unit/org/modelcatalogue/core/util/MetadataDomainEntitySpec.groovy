package org.modelcatalogue.core.util

import spock.lang.Specification
import spock.lang.Unroll

class MetadataDomainEntitySpec extends Specification {

    @Unroll
    void "#domain #id => #expected"(MetadataDomain domain, Long id, String expected) {

        expect:
        expected == MetadataDomainEntity.stringRepresentation(domain,id)

        where:
        domain                               | id   || expected
        null                                 | null || null
        MetadataDomain.ASSET                 | 16   || 'gorm://org.modelcatalogue.core.Asset:16'
        MetadataDomain.ASSET_FILE            | 1    || 'gorm://org.modelcatalogue.core.AssetFile:1'
        MetadataDomain.CATALOGUE_ELEMENT     | 2    || 'gorm://org.modelcatalogue.core.CatalogueElement:2'
        MetadataDomain.DATA_CLASS            | 3    || 'gorm://org.modelcatalogue.core.DataClass:3'
        MetadataDomain.DATA_ELEMENT          | 4    || 'gorm://org.modelcatalogue.core.DataElement:4'
        MetadataDomain.DATA_MODEL            | 5    || 'gorm://org.modelcatalogue.core.DataModel:5'
        MetadataDomain.DATA_MODEL_POLICY     | 6    || 'gorm://org.modelcatalogue.core.DataModelPolicy:6'
        MetadataDomain.DATA_TYPE             | 7    || 'gorm://org.modelcatalogue.core.DataType:7'
        MetadataDomain.ENUMERATED_TYPE       | 8    || 'gorm://org.modelcatalogue.core.EnumeratedType:8'
        MetadataDomain.EXTENSION_VALUE       | 9    || 'gorm://org.modelcatalogue.core.ExtensionValue:9'
        MetadataDomain.MAPPING               | 10   || 'gorm://org.modelcatalogue.core.Mapping:10'
        MetadataDomain.MEASUREMENT_UNIT      | 11   || 'gorm://org.modelcatalogue.core.MeasurementUnit:11'
        MetadataDomain.PRIMITIVE_TYPE        | 12   || 'gorm://org.modelcatalogue.core.PrimitiveType:12'
        MetadataDomain.REFERENCE_TYPE        | 13   || 'gorm://org.modelcatalogue.core.ReferenceType:13'
        MetadataDomain.RELATIONSHIP          | 14   || 'gorm://org.modelcatalogue.core.Relationship:14'
        MetadataDomain.RELATIONSHIP_METADATA | 15   || 'gorm://org.modelcatalogue.core.RelationshipMetadata:15'
        MetadataDomain.RELATIONSHIP_TYPE     | 16   || 'gorm://org.modelcatalogue.core.RelationshipType:16'
        MetadataDomain.RELATIONSHIP_TAG      | 17   || 'gorm://org.modelcatalogue.core.RelationshipTag:17'
    }

    @Unroll
    void "#domain #id #dataModelId link to: #expected"(MetadataDomain domain, Long id, Long dataModelId, String serverUrl, String expected) {

        expect:
        expected == MetadataDomainEntity.link(dataModelId, new MetadataDomainEntity(domain: domain, id: id), serverUrl)

        where:
        domain                               | id   | dataModelId | serverUrl               || expected
        null                                 | null | 2           | 'http://localhost:8080' || null
        MetadataDomain.ASSET                 | 16   | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/asset/16'
        MetadataDomain.ASSET_FILE            | 1    | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/assetFile/1'
        MetadataDomain.CATALOGUE_ELEMENT     | 2    | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/catalogueElement/2'
        MetadataDomain.DATA_CLASS            | 3    | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/dataClass/3'
        MetadataDomain.DATA_ELEMENT          | 4    | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/dataElement/4'
        MetadataDomain.DATA_MODEL            | 5    | 5           | 'http://localhost:8080' || 'http://localhost:8080/#/5/dataModel/5'
        MetadataDomain.DATA_MODEL_POLICY     | 6    | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/dataModelPolicy/6'
        MetadataDomain.DATA_TYPE             | 7    | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/dataType/7'
        MetadataDomain.ENUMERATED_TYPE       | 8    | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/enumeratedType/8'
        MetadataDomain.EXTENSION_VALUE       | 9    | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/extensionValue/9'
        MetadataDomain.MAPPING               | 10   | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/mapping/10'
        MetadataDomain.MEASUREMENT_UNIT      | 11   | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/measurementUnit/11'
        MetadataDomain.PRIMITIVE_TYPE        | 12   | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/primitiveType/12'
        MetadataDomain.REFERENCE_TYPE        | 13   | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/referenceType/13'
        MetadataDomain.RELATIONSHIP          | 14   | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/relationship/14'
        MetadataDomain.RELATIONSHIP_METADATA | 15   | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/relationshipMetadata/15'
        MetadataDomain.RELATIONSHIP_TYPE     | 16   | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/relationshipType/16'
        MetadataDomain.RELATIONSHIP_TAG      | 17   | 2           | 'http://localhost:8080' || 'http://localhost:8080/#/2/relationshipTag/17'
    }

    @Unroll
    void "#str => #domain #id"(String str, MetadataDomain domain, Long id) {

        when:
        MetadataDomainEntity expected = MetadataDomainEntity.of(str)

        then:
        expected?.domain == domain
        expected?.id == id

        where:
        str                                                       | domain                               | id
        'foo://org.modelcatalogue.core.Asset:16'                  | null                                 | null
        'gorm://org.modelcatalogue.core.Asset:bla'                | null                                 | null
        'gorm://org.modelcatalogue.core.Asset:16'                 | MetadataDomain.ASSET                 | 16
        'gorm://org.modelcatalogue.core.AssetFile:16'             | MetadataDomain.ASSET_FILE            | 16
        'gorm://org.modelcatalogue.core.CatalogueElement:16'      | MetadataDomain.CATALOGUE_ELEMENT     | 16
        'gorm://org.modelcatalogue.core.DataClass:16'             | MetadataDomain.DATA_CLASS            | 16
        'gorm://org.modelcatalogue.core.DataElement:16'           | MetadataDomain.DATA_ELEMENT          | 16
        'gorm://org.modelcatalogue.core.DataModel:16'             | MetadataDomain.DATA_MODEL            | 16
        'gorm://org.modelcatalogue.core.DataModelPolicy:16'       | MetadataDomain.DATA_MODEL_POLICY     | 16
        'gorm://org.modelcatalogue.core.DataType:16'              | MetadataDomain.DATA_TYPE             | 16
        'gorm://org.modelcatalogue.core.EnumeratedType:16'        | MetadataDomain.ENUMERATED_TYPE       | 16
        'gorm://org.modelcatalogue.core.ExtensionValue:16'        | MetadataDomain.EXTENSION_VALUE       | 16
        'gorm://org.modelcatalogue.core.Mapping:16'               | MetadataDomain.MAPPING               | 16
        'gorm://org.modelcatalogue.core.MeasurementUnit:16'       | MetadataDomain.MEASUREMENT_UNIT      | 16
        'gorm://org.modelcatalogue.core.PrimitiveType:16'         | MetadataDomain.PRIMITIVE_TYPE        | 16
        'gorm://org.modelcatalogue.core.ReferenceType:16'         | MetadataDomain.REFERENCE_TYPE        | 16
        'gorm://org.modelcatalogue.core.Relationship:16'          | MetadataDomain.RELATIONSHIP          | 16
        'gorm://org.modelcatalogue.core.RelationshipMetadata:16'  | MetadataDomain.RELATIONSHIP_METADATA | 16
        'gorm://org.modelcatalogue.core.RelationshipType:16'      | MetadataDomain.RELATIONSHIP_TYPE     | 16
        'gorm://org.modelcatalogue.core.RelationshipTag:16'       | MetadataDomain.RELATIONSHIP_TAG      | 16

    }
}
