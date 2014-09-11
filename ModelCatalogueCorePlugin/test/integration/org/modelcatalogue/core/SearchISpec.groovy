package org.modelcatalogue.core

import groovy.util.slurpersupport.GPathResult
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

    @Shared
    RelationshipService relationshipService
    @Shared
    def grailsApplication, de, vd, cd, mod


    def setupSpec(){
        loadFixtures()
        de = DataElement.findByName("auth7")
        vd = ValueDomain.findByName("value domain Celsius")
        cd = ConceptualDomain.findByName("public libraries")
        mod = Model.findByName("book")
        relationshipService = new RelationshipService()



        relationshipService.link(cd, mod, RelationshipType.findByName("context"))
        relationshipService.link(mod, de, RelationshipType.findByName("containment"))
    }

    def cleanupSpec() {
    }


    @Unroll
    def "#no - text search for #className "(){

        ResultRecorder recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePlugin/target/xml-samples/modelcatalogue/core",
                "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
                className[0].toLowerCase() + className.substring(1)
        )
        JSONElement json
        GPathResult xml

        expect:
        def domain = grailsApplication.getArtefact("Domain", "org.modelcatalogue.core.${className}")?.getClazz()
        def expectedResult = domain.findByName(expectedResultName)

        when:
        controller.response.format = response
        controller.params.search = searchString
        controller.search()
        String recordName = "searchElement${no}"
        if(response=="json"){
            json = controller.response.json
            recorder.recordResult recordName, json
        }else{
            xml = controller.response.xml
            recorder.recordResult recordName, xml
        }

        then:
        if(json){
            assert json
            assert json.total == total
            assert json.list.get(0).id == expectedResult.id
            assert json.list.get(0).name == expectedResult.name
        }else if(xml){
            assert xml
            assert xml.@success.text() == "true"
            assert xml.@size == total
            assert xml.@total == total
            assert xml.@offset.text() == "0"
            assert xml.@page.text() ==  "10000"
            assert xml.element
            assert xml.element.size() ==  total
            assert xml.depthFirst().find {  it.name == expectedResult.name }
        }else{
            throw new AssertionError("no result returned")
        }

        where:

        no| className           | controller                          | searchString                    | response  | expectedResultName        | total
        1 | "DataType"          | new DataTypeController()            | "boolean"                       | "json"    | "boolean"                 | 1
        2 | "DataType"          | new DataTypeController()            | "xdfxdf"                        | "json"    | "boolean"                 | 1
        3 | "DataType"          | new DataTypeController()            | "boolean"                       | "xml"     | "boolean"                 | 1
        4 | "DataType"          | new DataTypeController()            | "xdfxdf"                        | "xml"     | "boolean"                 | 1
        5 | "DataElement"       | new DataElementController()         | "de_author1"                         | "json"    | "DE_author1"              | 1
        6 | "DataElement"       | new DataElementController()         | "de_author1"                         | "xml"     | "DE_author1"              | 1
        7 | "ConceptualDomain"  | new ConceptualDomainController()    | "domain for public libraries"   | "json"    | "public libraries"        | 1
        8 | "ConceptualDomain"  | new ConceptualDomainController()    | "domain for public libraries"   | "xml"     | "public libraries"        | 1
        9 | "EnumeratedType"    | new EnumeratedTypeController()      | "sub1"                          | "json"    | "sub1"                    | 1
       10 | "EnumeratedType"    | new EnumeratedTypeController()      | "sub1"                          | "xml"     | "sub1"                    | 1
       11 | "MeasurementUnit"   | new MeasurementUnitController()     | "°C"                            | "json"    | "Degrees Celsius"         | 1
       12 | "MeasurementUnit"   | new MeasurementUnitController()     | "°C"                            | "xml"     | "Degrees Celsius"         | 1
       13 | "Model"             | new ModelController()               | "Jabberwocky"                   | "json"    | "chapter1"                | 1
       14 | "Model"             | new ModelController()               | "Jabberwocky"                   | "xml"     | "chapter1"                | 1
       15 | "ValueDomain"       | new ValueDomainController()         | "domain Celsius"                | "json"    | "value domain Celsius"    | 1
       16 | "ValueDomain"       | new ValueDomainController()         | "domain Celsius"                | "xml"     | "value domain Celsius"    | 1
       17 | "RelationshipType"  | new RelationshipTypeController()    | "context"                       | "json"    | "context"                 | 1
       18 | "RelationshipType"  | new RelationshipTypeController()    | "context"                       | "xml"     | "context"                 | 1
// search in nested elements not supported
//       19 | "ValueDomain"       | new ValueDomainController()         | "°F"                            | "xml"     | "value domain Fahrenheit" | 1
//       20 | "EnumeratedType"    | new EnumeratedTypeController()      | "male"                          | "json"    | "gender"                  | 1
//       21 | "EnumeratedType"    | new EnumeratedTypeController()      | "male"                          | "xml"     | "gender"                  | 1
//       22 | "DataElement"       | new DataElementController()         | "metadata"                      | "xml"     | "DE_author1"              | 1

    }

    @Unroll
    def "#no -  search model catalogue - paginate results"(){

        def controller = new SearchController()
        ResultRecorder recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePlugin/target/xml-samples/modelcatalogue/core",
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
                [1, 10, 10, 0, 22, "/search/?search=domain&max=10&offset=10",                      "",                                                              "domain"],
                [2,  5,  5, 0, 22, "/search/?search=domain&max=5&sort=name&order=ASC&offset=5",    "",                                                              "domain", "name",   "ASC"],
                [3,  2,  2, 6, 22, "/search/?search=domain&max=2&sort=name&order=ASC&offset=8",    "/search/?search=domain&max=2&sort=name&order=ASC&offset=4",     "domain", "name",   "ASC"],
                [4,  4,  4, 1, 22, "/search/?search=domain&max=4&sort=name&order=ASC&offset=5",    "",                                                              "domain", "name",   "ASC"],
                [5,  2,  2, 2, 22, "/search/?search=domain&max=2&sort=name&order=ASC&offset=4",    "/search/?search=domain&max=2&sort=name&order=ASC&offset=0",     "domain", "name",   "ASC"],
                [6,  2,  2, 4, 22, "/search/?search=domain&max=2&sort=name&offset=6",              "/search/?search=domain&max=2&sort=name&offset=2",               "domain", "name",   ""],
                [7,  2,  2, 4, 22, "/search/?search=domain&max=2&offset=6",                        "/search/?search=domain&max=2&offset=2",                         "domain", null,     null]
        ]
    }

@Unroll
    def "#no - bad search params"(){

        def controller = new SearchController()
        ResultRecorder recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePlugin/target/xml-samples/modelcatalogue/core",
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
