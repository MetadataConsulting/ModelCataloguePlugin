package uk.co.brc.modelcatalogue

import grails.test.spock.IntegrationSpec
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.web.json.JSONElement
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.DefaultResultRecorder
import org.modelcatalogue.core.util.ResultRecorder
import org.modelcatalogue.fixtures.FixturesLoader
import spock.lang.Shared
import spock.lang.Unroll

/**
 * Created by adammilward on 17/03/2014.
 */
class ElasticSearchISpec extends IntegrationSpec{

    //runs ok in integration test (test-app :integration), fails as part of test-app (Grails Bug) - uncomment to run
//RE: http://jira.grails.org/browse/GRAILS-11047?page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel


    @Shared
    def grailsApplication, elasticSearchService, initCatalogueService

    def setupSpec(){
        FixturesLoader fixturesLoader = new FixturesLoader("../ModelCatalogueCorePlugin/fixtures")

        Map fixtures = fixturesLoader.load("dataTypes/*", "conceptualDomains/*","dataElements/*","enumeratedTypes/*","measurementUnits/*","models/*","relationshipTypes/*")

        fixtures.each{ key, value ->
            value.save()
        }

        initCatalogueService.initDefaultRelationshipTypes()
        def de = DataElement.findByName("DE_author1")
      //  def vd = ValueDomain.findByName("value domain Celsius")
        def cd = ConceptualDomain.findByName("public libraries")
        def mod = Model.findByName("book")

        RelationshipService.link(cd, mod, RelationshipType.findByName("context"))
   //     Relationship.link(de, vd, RelationshipType.findByName("instantiation"))
        RelationshipService.link(mod, de, RelationshipType.findByName("containment"))

        elasticSearchService.index()
    }

    def cleanup(){
    }


    @Unroll
    def "#no - text search for resource "(){

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
            assert xml.@total == total
            assert xml.@offset.text() == "0"
            assert xml.@page.text() ==  "10"
            assert xml.element
            assert xml.depthFirst().find {  it.name == expectedResult.name }
        }else{
            throw new AssertionError("no result returned")
        }

        where:

        no| className           | controller                          | searchString                    | response  | expectedResultName        | total
        1 | "DataType"          | new DataTypeController()            | "boolean"                       | "json"    | "boolean"                 | 2
        2 | "DataType"          | new DataTypeController()            | "xdfxdf"                        | "json"    | "boolean"                 | 1
        3 | "DataType"          | new DataTypeController()            | "boolean"                       | "xml"     | "boolean"                 | 2
        4 | "DataType"          | new DataTypeController()            | "xdfxdf"                        | "xml"     | "boolean"                 | 1
        5 | "DataElement"       | new DataElementController()         | "XXX_1"                         | "json"    | "DE_author1"              | 1
        6 | "DataElement"       | new DataElementController()         | "XXX_1"                         | "xml"     | "DE_author1"              | 1
        7 | "ConceptualDomain"  | new ConceptualDomainController()    | "domain for public libraries"   | "json"    | "public libraries"        | 12
        8 | "ConceptualDomain"  | new ConceptualDomainController()    | "domain for public libraries"   | "xml"     | "public libraries"        | 12
        9 | "EnumeratedType"    | new EnumeratedTypeController()      | "sub1"                          | "json"    | "sub1"                    | 1
        10 | "EnumeratedType"    | new EnumeratedTypeController()      | "sub1"                          | "xml"     | "sub1"                    | 1
        11 | "MeasurementUnit"   | new MeasurementUnitController()     | "°C"                            | "json"    | "Degrees of Celsius"      | 1
        12 | "MeasurementUnit"   | new MeasurementUnitController()     | "°C"                            | "xml"     | "Degrees of Celsius"      | 1
        13 | "Model"             | new ModelController()               | "Jabberwocky"                   | "json"    | "chapter1"                | 1
        14 | "Model"             | new ModelController()               | "Jabberwocky"                   | "xml"     | "chapter1"                | 1
   //     15 | "ValueDomain"       | new ValueDomainController()         | "domain Celsius"                | "json"    | "value domain Celsius"    | 4
   //     16 | "ValueDomain"       | new ValueDomainController()         | "domain Celsius"                | "xml"     | "value domain Celsius"    | 4
        17 | "RelationshipType"  | new RelationshipTypeController()    | "context"                       | "json"    | "context"                 | 1
        18 | "RelationshipType"  | new RelationshipTypeController()    | "context"                       | "xml"     | "context"                 | 1
  //      19 | "ValueDomain"       | new ValueDomainController()         | "°F"                            | "xml"     | "value domain Fahrenheit" | 1
        20 | "EnumeratedType"    | new EnumeratedTypeController()      | "male"                          | "json"    | "gender"                  | 1
        21 | "EnumeratedType"    | new EnumeratedTypeController()      | "male"                          | "xml"     | "gender"                  | 1
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
        //      no, size, max , off. tot. next, previous, search, previous, sort, order
                [1, 10, 10, 0, 12, "/search/domain?max=10&offset=10", "",  "domain"],
                [2, 2, 2,  0, 12, "/search/domain?max=2&sort=name&order=ASC&offset=2", "", "domain", "name", "ASC"],
                [3, 2, 2,  2, 12, "/search/domain?max=2&sort=name&order=ASC&offset=4", "/search/domain?max=2&sort=name&order=ASC&offset=0", "domain", "name", "ASC"],
                [4, 1, 1,  1, 12, "/search/domain?max=1&sort=name&order=ASC&offset=2", "/search/domain?max=1&sort=name&order=ASC&offset=0", "domain", "name", "ASC"],
                [5, 2, 2,  2, 12, "/search/domain?max=2&sort=name&order=ASC&offset=4", "/search/domain?max=2&sort=name&order=ASC&offset=0", "domain", "name", "ASC"],
                [6, 1, 1,  1, 12, "/search/domain?max=1&sort=name&offset=2", "/search/domain?max=1&sort=name&offset=0", "domain", "name", ""]
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
                [1, 2, 2, 4, 7, "domain", "name", "blah blah blah", "Illegal argument: No enum constant org.elasticsearch.search.sort.SortOrder.BLAH BLAH BLAH"],
                [2, 2, 2, 4, 7, "", "name","", "No query string to search on"]
        ]
    }


}

