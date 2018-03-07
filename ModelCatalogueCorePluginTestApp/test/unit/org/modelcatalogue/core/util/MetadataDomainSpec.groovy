package org.modelcatalogue.core.util

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
import spock.lang.Specification
import spock.lang.Unroll

class MetadataDomainSpec extends Specification {

    @Unroll
    void "#catalogueelement => #expected"(CatalogueElement catalogueelement, MetadataDomain expected) {
        expect:
        expected == MetadataDomain.of(catalogueelement)

        where:
        catalogueelement                | expected
        new Asset()                     | MetadataDomain.ASSET
        new DataClass()                 | MetadataDomain.DATA_CLASS
        new DataElement()               | MetadataDomain.DATA_ELEMENT
        new DataModel()                 | MetadataDomain.DATA_MODEL
        new DataType()                  | MetadataDomain.DATA_TYPE
        new EnumeratedType()            | MetadataDomain.ENUMERATED_TYPE
        new MeasurementUnit()           | MetadataDomain.MEASUREMENT_UNIT
        new PrimitiveType()             | MetadataDomain.PRIMITIVE_TYPE
        new ReferenceType()             | MetadataDomain.REFERENCE_TYPE
        null                            | MetadataDomain.CATALOGUE_ELEMENT
    }
}
