package org.modelcatalogue.core.util

import org.modelcatalogue.core.*
import spock.lang.Specification

class CatalogueElementFinderSpec extends Specification {

    def "Find all subclasses"() {
        def candidatesClasses = CatalogueElementFinder.catalogueElementClasses

        expect:
        DataElement.name        in candidatesClasses
        DataType.name           in candidatesClasses
        MeasurementUnit.name    in candidatesClasses
        Model.name              in candidatesClasses
        ValueDomain.name        in candidatesClasses
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
        assetTypes.size() == 3

        CatalogueElement.name in assetTypes
        Asset.name in assetTypes


    }

}
