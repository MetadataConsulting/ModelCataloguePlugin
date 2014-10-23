package org.modelcatalogue.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adammilward on 05/02/2014.
 */
@Mock([Classification, DataElement, Model, PublishedElement])
class ClassificationDomainSpec extends Specification {


    @Unroll
    def "Classiciation creation for #args results in #validates"()
    {

        expect:

        Classification.list().isEmpty()

        when:
		args.modelCatalogueId = "http://example.com/123"
        def conceptInstance = new Classification(args)

        conceptInstance.save()

        then:

        (conceptInstance.errors.errorCount == 0) == validates

        where:

        validates  | args
        false      | [name: "", description: "test concept description"]
        false      | [name:"t"*256, description: "test concept description"]
        false      | [name:"test concept", description: "t"*2001]
        true       | [name:"test concept", description: "test concept description"]

    }

    def "add remove data elements and models from classifiations"(){

        setup:
        def model = new Model(name: "testmodel").save(flush:true)
        def dataElement = new DataElement(name: "testDE").save(flush:true)
        def classification = new Classification(name: "testDE").save(flush:true)

        when:
        classification.addToClassifies(model)
        classification.addToClassifies(dataElement)

        then:
        classification.classifies.contains(dataElement)
        classification.classifies.contains(model)

    }
}
