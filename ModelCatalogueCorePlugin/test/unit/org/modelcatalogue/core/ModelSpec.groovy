package org.modelcatalogue.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adammilward on 05/02/2014.
 *
 * Models are like xml complex types
 * They contain data elements
 */

@Mock([Model, ExtensionValue])
class ModelSpec extends Specification {

    @Unroll
    def "Model creation for #args results in #validates"() {

        expect:
        Model.list().isEmpty()

        when:
        Model modelInstance = new Model(args)
        modelInstance.save()

        then:
        !modelInstance.hasErrors() == validates
        modelInstance.versionNumber == 1
        modelInstance.list().size() == size
        modelInstance.modelCatalogueId == modelCatalogueId

        where:

        no | validates | size | args | modelCatalogueId
        1 | false      | 0    | [name: "", description: "test model description"] | null
        2 | false      | 0    | [name: "t" * 256, description: "test model description"] | null
        3 | false      | 0    | [name: "test model", description: "t" * 2001] | null
        4 | false      | 0    | [name: "test model", description: "test model description", modelCatalogueId: "MC_12asd_3"] | "MC_12asd_3"
        5 | true       | 1    | [name: "test model", description: "test model description"] | "MC_1_1"
        6 | true       | 1    | [name: "test model2", description: "test model description", modelCatalogueId: "MC_12_3"] | "MC_12_3"

    }

    @Unroll
    def "create a new model and extend it to include additional metadata"() {

        expect:

        Model.list().isEmpty()

        when:

        def dataExtension = ["ip": "x.xx.xx.xx", "owner": "BRC_Informatics"]

        Model modelInstance = new Model(name: "result1", description: "this is the the result description")


        modelInstance.save()
        modelInstance.ext.putAll dataExtension

        then:

        !modelInstance.hasErrors()
        modelInstance.ext.ip == "x.xx.xx.xx"
        modelInstance.ext.owner == "BRC_Informatics"


    }


    def "Get link info"() {


        Model modelInstance = new Model(name: "result1", description: "this is the the result description").save(flush: true)

        expect:
        modelInstance
        modelInstance.id

        modelInstance.info
        modelInstance.info.name == modelInstance.name
        modelInstance.info.id == modelInstance.id
        modelInstance.info.link == "/model/${modelInstance.id}"
    }

}

