package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.marshalling.*
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Ensures that given controller does not contain any custom logic
 */
@Unroll
class NoCustomLogicSpec extends Specification {

    def "#cls does not have any custom logic and directly inherits from #superclass"() {
        expect:
        cls.superclass == superclass
        cls.declaredMethods.findAll { !it.synthetic } .size() == numberOfVirtualMethods

        where:
        cls                         | superclass                     | numberOfVirtualMethods
        ConceptualDomainController  | CatalogueElementController     | MeasurementUnitController.declaredMethods.findAll { !it.synthetic } .size()
        DataElementController       | CatalogueElementController     | MeasurementUnitController.declaredMethods.findAll { !it.synthetic } .size()
        //DataTypeController          | CatalogueElementController     | MeasurementUnitController.declaredMethods.findAll { !it.synthetic } .size()
        //EnumeratedTypeController    | CatalogueElementController     | MeasurementUnitController.declaredMethods.findAll { !it.synthetic } .size() + customAndTestedMethodsCount
        ModelController             | CatalogueElementController     | MeasurementUnitController.declaredMethods.findAll { !it.synthetic } .size()
        //RelationshipTypeController  | AbstractRestfulController      | MeasurementUnitController.declaredMethods.findAll { !it.synthetic } .size() + customAndTestedMethodsCount
        Elements                    | ListWrapper                    | 0
        ConceptualDomainMarshaller  | CatalogueElementMarshallers    | 0
        //DataTypeMarshaller          | CatalogueElementMarshallers    | 0
        ModelMarshaller             | ExtendibleElementMarshallers   | 0
    }

}
