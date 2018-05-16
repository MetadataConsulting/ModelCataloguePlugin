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
import org.modelcatalogue.core.Tag
import org.modelcatalogue.core.ValidationRule
import spock.lang.Specification
import spock.lang.Unroll

class MetadataDomainSpec extends Specification {

    @Unroll
    def "For #clazz.simpleName metadataDomain is: #expected"(MetadataDomain expected, Class clazz) {
        expect:
        expected == MetadataDomain.ofClass(clazz)

        where:
        expected                         | clazz
        MetadataDomain.ASSET             | Asset.class
        MetadataDomain.DATA_CLASS        | DataClass.class
        MetadataDomain.DATA_ELEMENT      | DataElement.class
        MetadataDomain.DATA_MODEL        | DataModel.class
        MetadataDomain.ENUMERATED_TYPE   | EnumeratedType.class
        MetadataDomain.MEASUREMENT_UNIT  | MeasurementUnit.class
        MetadataDomain.PRIMITIVE_TYPE    | PrimitiveType.class
        MetadataDomain.REFERENCE_TYPE    | ReferenceType.class
        MetadataDomain.DATA_TYPE         | DataType.class
        MetadataDomain.BUSINESS_RULE     | ValidationRule.class
        MetadataDomain.TAG               | Tag.class
        MetadataDomain.CATALOGUE_ELEMENT | MetadataDomainSpec.class // default case
    }

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
