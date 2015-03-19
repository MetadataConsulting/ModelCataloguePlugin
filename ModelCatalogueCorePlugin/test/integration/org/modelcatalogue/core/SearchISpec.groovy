package org.modelcatalogue.core

import org.codehaus.groovy.grails.web.json.JSONElement
import org.modelcatalogue.core.util.DefaultResultRecorder
import org.modelcatalogue.core.util.ResultRecorder
import spock.lang.Shared
import spock.lang.Unroll

/**
 * Created by adammilward on 05/02/2014.
 */
class SearchISpec extends AbstractIntegrationSpec{

 //runs ok in integration test (test-app :integration), fails as part of test-app (Grails Bug) - uncomment to run
//RE: http://jira.grails.org/browse/GRAILS-11047?page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel

    RelationshipService relationshipService
    def grailsApplication


    def setup(){
        loadFixtures()
    }

    @Unroll
    def "#no - text search for #className "(){

        ResultRecorder recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
                className[0].toLowerCase() + className.substring(1)
        )


        expect:
        def domain = grailsApplication.getArtefact("Domain", "org.modelcatalogue.core.${className}")?.getClazz()
        def expectedResult = domain.findByName(expectedResultName)

        when:
        controller.response.format = 'json'
        controller.params.search = searchString

        if (status) {
            controller.params.status = status
        }

        controller.search(10)
        String recordName = "searchElement${no}"
        JSONElement json = controller.response.json
        recorder.recordResult recordName, json


        then:
        assert json
        assert json.total == total
        assert !total || json.list.get(0).id == expectedResult.id
        assert !total || json.list.get(0).name == expectedResult.name

        where:

        no| className           | controller                          | searchString                    | response  | expectedResultName        | total     | status
        1 | "DataType"          | new DataTypeController()            | "boolean"                       | "json"    | "boolean"                 | 1         | null
        2 | "DataType"          | new DataTypeController()            | "xdfxdf"                        | "json"    | "boolean"                 | 1         | null
        5 | "DataElement"       | new DataElementController()         | "de_author1"                    | "json"    | "DE_author1"              | 1         | null
        9 | "EnumeratedType"    | new EnumeratedTypeController()      | "sub1"                          | "json"    | "sub1"                    | 1         | null
       11 | "MeasurementUnit"   | new MeasurementUnitController()     | "°C"                            | "json"    | "Degrees Celsius"         | 1         | null
       13 | "Model"             | new ModelController()               | "Jabberwocky"                   | "json"    | "chapter1"                | 1         | null
       14 | "Model"             | new ModelController()               | "Jabberwocky"                   | "json"    | "chapter1"                | 0         | 'deprecated'
       15 | "ValueDomain"       | new ValueDomainController()         | "domain Celsius"                | "json"    | "value domain Celsius"    | 1         | null
       17 | "RelationshipType"  | new RelationshipTypeController()    | "classification"                | "json"    | "classification"          | 2         | null
       18 | "RelationshipType"  | new RelationshipTypeController()    | "classification"                | "json"    | "classification"          | 2         | null
    }

    @Unroll
    def "#no -  search model catalogue - paginate results"(){

        def controller = new SearchController()
        ResultRecorder recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
                "search"
        )

        when:
        controller.response.format = "json"
        controller.params.max = max
        controller.params.offset = offset
        controller.params.search = searchString
        controller.params.sort = sort
        controller.params.order = order
        controller.index()
        JSONElement json = controller.response.json
        String list = "list${no}"
        recorder.recordResult list, json

        then:
        json.success
        json.total == total
        json.offset == offset
        json.page == max
        json.list
        json.list.size() == size
        json.next == next
        json.previous == previous

        where:
        [no, size, max, offset, total, next, previous, searchString, sort, order] << getPaginationParameters()


    }



    protected static getPaginationParameters() {
        [
                // no, size, max, offset, total, next, previous, searchString, sort, order
                [1, 10, 10, 0, 75, "/search/?search=a&max=10&offset=10", "", "a"],
                [2, 5, 5, 0, 75, "/search/?search=a&max=5&sort=name&order=ASC&offset=5", "", "a", "name", "ASC"],
                [3, 2, 2, 6, 75, "/search/?search=a&max=2&sort=name&order=ASC&offset=8", "/search/?search=a&max=2&sort=name&order=ASC&offset=4", "a", "name", "ASC"],
                [4, 4, 4, 1, 75, "/search/?search=a&max=4&sort=name&order=ASC&offset=5", "", "a", "name", "ASC"],
                [5, 2, 2, 2, 75, "/search/?search=a&max=2&sort=name&order=ASC&offset=4", "/search/?search=a&max=2&sort=name&order=ASC&offset=0", "a", "name", "ASC"],
                [6, 2, 2, 4, 75, "/search/?search=a&max=2&sort=name&offset=6", "/search/?search=a&max=2&sort=name&offset=2", "a", "name", ""],
                [7, 2, 2, 4, 75, "/search/?search=a&max=2&offset=6", "/search/?search=a&max=2&offset=2", "a", null, null]
        ]
    }

@Unroll
    def "#no - bad search params"(){

        def controller = new SearchController()
        ResultRecorder recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
                "badSearch"
        )

        when:
        controller.response.format = "json"
        controller.params.max = max
        controller.params.offset = offset
        controller.params.search = searchString
        controller.params.sort = sort
        controller.params.order = order

        controller.index()
        JSONElement json = controller.response.json

        String list = "list${no}"

        recorder.recordResult list, json

        then:

        json.errors == error

        where:
        [no, size, max, offset, total, searchString, sort, order, error] << getBadParameters()




    }

    protected static getBadParameters() {
        [
                // no,size, max , off. tot. next, previous, searchstring                           , previous
                [2, 2, 2, 4, 7, "", "name","", "No query string to search on"]
        ]
    }


}
