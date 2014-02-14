package uk.co.mc.core

import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adammilward on 05/02/2014.
 *
 * A Data Element is an atomic unit used to describe data
 * i.e. <subject> xxxxxxx </subject> tag allows us to describes the data between the tags as being an DE_author
 *
 * DataElements are atomic therefore they CANNOT contain sub elements
 *
 * Data elements are instantiated by value domains
 * i.e. if we have a value domain with an enumerated list of subjects [science, history, politics] that we want
 * to apply to to a data element we can instantiate the data element
 * i.e. <subject valueDomain="subjects"> xxxxxxxxx </subject> and know what type of data to expect between the tags.
 * DataElements can be instantiated by different value domains depending on the context (see context relationship type)
 * i.e.
 * <book conceptualDomain="libraryA"><subject type="politicalSubjects"></subject></book>
 * <book conceptualDomain="libraryB"><subject type="historicalSubjects"></subject></book>
 *
 * DataElements can be contained in models:
 * i.e. <book><subject></subject</book> where book is the model and subject is the data element
 * this can be achieved using the containment relationship type (see containment relationship type)
 *
 *
 */
@Mock([DataElement, ExtensionValue])
class DataElementSpec extends Specification{

    @Unroll
    def "create a new data element from #args validates to #validates"(){

        expect:

        DataElement.list().isEmpty()

        when:

        DataElement dataElementInstance = new DataElement(args)

        dataElementInstance.save()

        then:

        !dataElementInstance.hasErrors()==validates
        dataElementInstance.versionNumber == 0.1
        DataElement.list().size() == size


        where:

        validates |   size    | args
        false     |   0       | [name: "x" * 256, description: "this is the the result description"]
        false     |   0       | [name: "x", description: "x"*2001]
        false     |   0       | [name: "result1", description: "this is the the result description", code:"x" * 256]
        true      |   1       | [name: "result1", description: "this is the the result description", code:"NHIC12341"]

    }

    @Unroll
    def "create a new data element and extend it to include additional metadata"(){

        expect:

        DataElement.list().isEmpty()

        when:

        def dataCollectionQualityExtension = ["oxford": "1", "cambridge": "3"]

        DataElement dataElementInstance = new DataElement(name: "result1", description: "this is the the result description")

        dataElementInstance.save()
        dataElementInstance.ext.putAll dataCollectionQualityExtension


        then:

        !dataElementInstance.hasErrors()
        dataElementInstance.ext.oxford == "1"
        dataElementInstance.ext.cambridge == "3"

    }


    def "check  EqualsAndHashCode works"(){

        when:
        def a = new DataElement(name:"test concept", description: "test concept description", code:"xxx", versionNumber: 0.1)
        def b = new DataElement(name:"test concept", description: "test concept description", code:"xxx", versionNumber: 0.1)
        def c = new DataElement(name:"test conceptasdsfdfsad", description: "test concept description", versionNumber: 0.1)
        def d = new DataElement(name:"test concept", description: "test concept description", code:"xxx", versionNumber: 0.1)
        def e = new DataElement(name:"test concept", description: "test concept description", code:"xxx", versionNumber: 0.1)
        def f = new DataElement(name:"test concept", description: "test concept description", code:"xxx", versionNumber: 0.2)
        def ext = new ExtensionValue(name: "xxx", value: "x", element: d).save()
        d.addToExtensions(ext)
        e.addToExtensions(ext)
        assert(!d.save().hasErrors())
        assert(!e.save().hasErrors())

        then:
        a.equals(b)
        b.equals(a)
        !a.equals(c)
        !a.equals(d)
        !a.equals(f)
        d.equals(e)

    }
}
