package org.modelcatalogue.core.util

import org.modelcatalogue.core.*
import spock.lang.Specification

class CatalogueElementFinderSpec extends Specification {

    def "Find all subclasses"() {
        def candidatesClasses = CatalogueElementFinder.catalogueElementClasses

        expect:
        ConceptualDomain.name   in candidatesClasses
        DataElement.name        in candidatesClasses
        DataType.name           in candidatesClasses
        EnumeratedType.name     in candidatesClasses
        ExtensionValue.name     in candidatesClasses
        MeasurementUnit.name    in candidatesClasses
        Model.name              in candidatesClasses
        PublishedElement.name   in candidatesClasses
        ValueDomain.name        in candidatesClasses
        CatalogueElement.name   in candidatesClasses

    }

}
