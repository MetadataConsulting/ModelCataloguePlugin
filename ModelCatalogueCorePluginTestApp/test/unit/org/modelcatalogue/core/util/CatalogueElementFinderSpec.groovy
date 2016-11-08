package org.modelcatalogue.core.util

import org.modelcatalogue.builder.api.ModelCatalogueTypes
import org.modelcatalogue.core.*
import spock.lang.Specification

class CatalogueElementFinderSpec extends Specification {

    def setup() {
        ModelCatalogueTypes.CATALOGUE_ELEMENT.implementation = CatalogueElement
        ModelCatalogueTypes.DATA_MODEL.implementation = DataModel
        ModelCatalogueTypes.DATA_CLASS.implementation = DataClass
        ModelCatalogueTypes.DATA_ELEMENT.implementation = DataElement
        ModelCatalogueTypes.DATA_TYPE.implementation = DataType
        ModelCatalogueTypes.MEASUREMENT_UNIT.implementation = MeasurementUnit
        ModelCatalogueTypes.ENUMERATED_TYPE.implementation = EnumeratedType
        ModelCatalogueTypes.PRIMITIVE_TYPE.implementation = PrimitiveType
        ModelCatalogueTypes.REFERENCE_TYPE.implementation = ReferenceType
        ModelCatalogueTypes.VALIDATION_RULE.implementation = ValidationRule
    }

    def "Find all subclasses"() {
        def candidatesClasses = CatalogueElementFinder.catalogueElementClasses

        expect:
        DataElement.name        in candidatesClasses
        DataType.name           in candidatesClasses
        MeasurementUnit.name    in candidatesClasses
        DataClass.name          in candidatesClasses
        CatalogueElement.name   in candidatesClasses

    }


    def "Find all element types"() {
        when:
        def relTypeTypes = CatalogueElementFinder.getAllTypesNames(RelationshipType)

        then:
        !relTypeTypes

        when:
        def ceTypes = CatalogueElementFinder.getAllTypesNames(CatalogueElement)

        then:
        ceTypes == [CatalogueElement.name]

        when:
        def assetTypes = CatalogueElementFinder.getAllTypesNames(Asset)

        then:
        assetTypes
        assetTypes.size() == 2

        CatalogueElement.name in assetTypes
        Asset.name in assetTypes


    }

}
