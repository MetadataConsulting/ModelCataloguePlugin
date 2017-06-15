package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import spock.lang.Unroll

/**
 * Created by adammilward on 05/02/2014.
 *
 * A Data Element is an atomic unit used to describe data
 * i.e. <subject> xxxxxxx </subject> tag allows us to describes the data between the tags as being an DE_author
 *
 * DataElements are atomic therefore they CANNOT contains sub elements
 *
 * Data elements are instantiated by value domains
 * i.e. if we have a value domain with an enumerated list of subjects [science, history, politics] that we want
 * to apply to to a data element we can instantiate the data element
 * i.e. <subject dataTypes="subjects"> xxxxxxxxx </subject> and know what type of data to expect between the tags.
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
class DataElementSpec extends IntegrationSpec {

    @Unroll
    def "#no create a new data element from #args validates to #validates"() {
        int initialSize = DataElement.count()

        when:

        DataElement dataElementInstance = new DataElement(args)
        dataElementInstance.modelCatalogueId = args.modelCatalogueId
        dataElementInstance.save(flush: true)


        then:
        dataElementInstance.id != null == validates
        (dataElementInstance.errors.errorCount == 0) == validates
        dataElementInstance.versionNumber == 1
        DataElement.list().size() == size + initialSize
        dataElementInstance.modelCatalogueId == args.modelCatalogueId


        where:

        no | validates | size | args
        1 | false     | 0    | [name: "x" * 256, description: "this is the the result description"]
        2 | false     | 0    | [name: "x", description: "x" * 20001]
        3 | false     | 0    | [name: "result1", description: "this is the the result description", modelCatalogueId: "x" * 256]
        4 | true      | 1    | [name: "result1", description: "this is the the result description", modelCatalogueId: "http://example.com/123"]
        5 | true      | 1    | [name: "result2", description: "this is the the result description"]
        6 | true      | 1    | [name: "result1", description: "this is the the result description", modelCatalogueId: "MC_12asd33_3"]
    }

    def "create a new data element and extend it to include additional metadata"() {
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


}
