package uk.co.mc.core

import grails.test.mixin.TestFor
import grails.test.spock.IntegrationSpec
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject
import spock.lang.Shared
import spock.lang.Unroll
import uk.co.mc.core.util.ResultRecorder
import uk.co.mc.core.util.marshalling.DataElementMarshaller

/**
 * Created by adammilward on 05/02/2014.
 */
@Mixin(ResultRecorder)
class SearchISpec extends AbstractIntegrationSpec{

 //runs ok in integration test (test-app :integration), fails as part of test-app (Grails Bug) - uncomment to run
//RE: http://jira.grails.org/browse/GRAILS-11047?page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel


    @Shared
    def grailsApplication, elasticSearchService

    def setupSpec(){
        loadFixtures()
        RelationshipType.initDefaultRelationshipTypes()
        def de = DataElement.findByName("DE_author1")
        def vd = ValueDomain.findByName("value domain Celsius")
        def cd = ConceptualDomain.findByName("public libraries")
        def mod = Model.findByName("book")

        Relationship.link(cd, mod, RelationshipType.findByName("context"))
        Relationship.link(de, vd, RelationshipType.findByName("instantiation"))
        Relationship.link(mod, de, RelationshipType.findByName("containment"))

        elasticSearchService.index()
    }

    def cleanup(){
    }


    def "search model catalogue - paginate results"(){


        

    }


    @Unroll
    def "#no - text search for resource "(){

        JSONElement json
        GPathResult xml

        expect:
        def domain = grailsApplication.getArtefact("Domain", "uk.co.mc.core.${className}")?.getClazz()
        def expectedResult = domain.findByName(expectedResultName)

        when:
        controller.response.format = response
        controller.params.search = searchString
        controller.search()

        String recordName = "searchElement${no}"

        if(response=="json"){
            json = controller.response.json
            recordResult recordName, json, className[0].toLowerCase() + className.substring(1)
        }else{
            xml = controller.response.xml
            recordResult recordName, xml, className[0].toLowerCase() + className.substring(1)
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
            assert xml.@page.text() ==  "0"
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
        5 | "DataElement"       | new DataElementController()         | "XXX_1"                         | "json"    | "DE_author1"              | 1
        6 | "DataElement"       | new DataElementController()         | "XXX_1"                         | "xml"     | "DE_author1"              | 1
        7 | "ConceptualDomain"  | new ConceptualDomainController()    | "domain for public libraries"   | "json"    | "public libraries"        | 3
        8 | "ConceptualDomain"  | new ConceptualDomainController()    | "domain for public libraries"   | "xml"     | "public libraries"        | 3
        9 | "EnumeratedType"    | new EnumeratedTypeController()      | "sub1"                          | "json"    | "sub1"                    | 1
       10 | "EnumeratedType"    | new EnumeratedTypeController()      | "sub1"                          | "xml"     | "sub1"                    | 1
       11 | "MeasurementUnit"   | new MeasurementUnitController()     | "°C"                            | "json"    | "Degrees of Celsius"      | 1
       12 | "MeasurementUnit"   | new MeasurementUnitController()     | "°C"                            | "xml"     | "Degrees of Celsius"      | 1
       13 | "Model"             | new ModelController()               | "Jabberwocky"                   | "json"    | "chapter1"                | 1
       14 | "Model"             | new ModelController()               | "Jabberwocky"                   | "xml"     | "chapter1"                | 1
       15 | "ValueDomain"       | new ValueDomainController()         | "domain Celsius"                | "json"    | "value domain Celsius"    | 4
       16 | "ValueDomain"       | new ValueDomainController()         | "domain Celsius"                | "xml"     | "value domain Celsius"    | 4
       17 | "RelationshipType"  | new RelationshipTypeController()    | "context"                       | "json"    | "context"                 | 1
       18 | "RelationshipType"  | new RelationshipTypeController()    | "context"                       | "xml"     | "context"                 | 1
       19 | "ValueDomain"       | new ValueDomainController()         | "°F"                            | "xml"     | "value domain Fahrenheit" | 1
       20 | "EnumeratedType"    | new EnumeratedTypeController()      | "male"                          | "json"    | "gender"                  | 1
       21 | "EnumeratedType"    | new EnumeratedTypeController()      | "male"                          | "xml"     | "gender"                  | 1
       22 | "DataElement"       | new DataElementController()         | "metadata"                      | "xml"     | "DE_author1"              | 1

    }

}
