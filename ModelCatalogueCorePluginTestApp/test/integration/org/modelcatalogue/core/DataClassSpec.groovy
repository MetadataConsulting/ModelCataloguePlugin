package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import spock.lang.Unroll
/**
 * Created by adammilward on 05/02/2014.
 *
 * Models are like xml complex types
 * They contains data elements
 */

class DataClassSpec extends IntegrationSpec {

    @Unroll
    def "Model creation for #args results in #validates"() {
        int initialSize = DataClass.count()

        when:
        DataClass modelInstance = new DataClass(args)
        modelInstance.modelCatalogueId = args.modelCatalogueId
        modelInstance.save()

        then:
        (modelInstance.errors.errorCount == 0) == validates
        modelInstance.versionNumber == 1
        modelInstance.list().size() == size + initialSize
        modelInstance.modelCatalogueId == args.modelCatalogueId



        where:
        no | validates | size | args
        1  | false     | 0    | [name: "x" * 256, description: "this is the the result description"]
        2  | false     | 0    | [name: "x", description: "x" * 20001]
        3  | false     | 0    | [name: "result1", description: "this is the the result description", modelCatalogueId: "x" * 256]
        4  | true      | 1    | [name: "result1", description: "this is the the result description", modelCatalogueId: "http://example.com/123"]
        5  | true      | 1    | [name: "result2", description: "this is the the result description"]
        6  | true      | 1    | [name: "result1", description: "this is the the result description", modelCatalogueId: "MC_12asd33_3"]

    }

    @Unroll
    def "create a new model and extend it to include additional metadata"() {
        when:

        def dataExtension = ["ip": "x.xx.xx.xx", "owner": "BRC_Informatics"]

        DataClass modelInstance = new DataClass(name: "result1", description: "this is the the result description")


        modelInstance.save()
        modelInstance.ext.putAll dataExtension

        then:

        !modelInstance.hasErrors()
        modelInstance.ext.ip == "x.xx.xx.xx"
        modelInstance.ext.owner == "BRC_Informatics"


    }


    def "Get link info"() {


        DataClass modelInstance = new DataClass(name: "result1", description: "this is the the result description").save(flush: true)

        expect:
        modelInstance
        modelInstance.id

        modelInstance.info
        modelInstance.info.name == modelInstance.name
        modelInstance.info.id == modelInstance.id
        modelInstance.info.link == "/dataClass/${modelInstance.id}"
    }

}

