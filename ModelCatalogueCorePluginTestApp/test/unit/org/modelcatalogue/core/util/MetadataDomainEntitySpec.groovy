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
