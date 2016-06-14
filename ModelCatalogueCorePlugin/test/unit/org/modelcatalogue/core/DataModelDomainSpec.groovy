package org.modelcatalogue.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

@Mock([DataModel, DataElement, DataClass, CatalogueElement])
class DataModelDomainSpec extends Specification {


    @Unroll
    def "Data model creation for #args results in #validates"()
    {

        expect:

        DataModel.list().isEmpty()

        when:
		args.modelCatalogueId = "http://example.com/123"
        def dataModel = new DataModel(args)

        dataModel.save()

        then:

        (dataModel.errors.errorCount == 0) == validates

        where:

        validates  | args
        false      | [name: "", description: "test concept description"]
        false      | [name:"t"*256, description: "test concept description"]
        false      | [name:"test concept", description: "t"*20001]
        true       | [name:"test concept", description: "test concept description"]

    }

}
