package org.modelcatalogue.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adammilward on 05/02/2014.
 *
 * Models are like xml complex types
 * They contain data elements
 */
@TestFor(Model)
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

        where:

        validates | args
        false     | [name: "", description: "test model description"]
        false     | [name: "t" * 256, description: "test model description"]
        false     | [name: "test model", description: "t" * 2001]
        true      | [name: "test model", description: "test model description"]

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


        Model modelInstance = new Model(name: "result1", description: "this is the the result description").save()

        expect:
        modelInstance

        modelInstance.info
        modelInstance.info.name == modelInstance.name
        modelInstance.info.id == modelInstance.id
        modelInstance.info.link == "/model/${modelInstance.id}"
    }


    def "check  EqualsAndHashCode works"() {

        when:
        def a = new Model(name: "test concept", description: "test concept description", versionNumber: 0.1)
        def b = new Model(name: "test concept", description: "test concept description", versionNumber: 0.1)
        def c = new Model(name: "test conceptasdsfdfsad", description: "test concept description", versionNumber: 0.1)
        def d = new Model(name: "test concept", description: "test concept description", versionNumber: 0.1)
        def e = new Model(name: "test concept", description: "test concept description", versionNumber: 0.1)
        def f = new Model(name: "test concept", description: "test concept description", versionNumber: 0.2)
        def ext = new ExtensionValue(name: "xxx", extensionValue: "x", element: d).save()
        d.addToExtensions(ext)
        e.addToExtensions(ext)
        assert (!d.save().hasErrors())
        assert (!e.save().hasErrors())

        then:
        a.equals(b)
        b.equals(a)
        !a.equals(c)
        !a.equals(d)
        !a.equals(f)
        d.equals(e)

    }


}

