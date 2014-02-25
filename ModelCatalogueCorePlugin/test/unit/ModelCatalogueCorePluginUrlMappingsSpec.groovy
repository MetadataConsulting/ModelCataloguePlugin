import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import uk.co.mc.core.CatalogueElementController
import uk.co.mc.core.ConceptualDomainController
import uk.co.mc.core.DataElementController
import uk.co.mc.core.DataTypeController
import uk.co.mc.core.EnumeratedTypeController
import uk.co.mc.core.MeasurementUnitController
import uk.co.mc.core.ModelController
import uk.co.mc.core.ValueDomainController

@TestFor(ModelCatalogueCorePluginUrlMappings)
@Mock([
    ConceptualDomainController,
    DataElementController,
    DataTypeController,
    EnumeratedTypeController,
    MeasurementUnitController,
    ModelController,
    ValueDomainController
])
@Unroll
class ModelCatalogueCorePluginUrlMappingsSpec extends Specification {

    def "for method #method and url /api/modelCatalogue/core#url expect controller #controller and action #action with #paramsAssertions"() {
        expect:
        assertRestForwardUrlMapping(method, "/api/modelCatalogue/core$url", controller: controller, action: action, paramsAssertions)

        where:
        [method, url, controller, action, paramsAssertions] << generateAssertionsForCatalogueElementControllers('conceptualDomain', 'dataElement', 'dataType', 'enumeratedType', 'measurementUnit', 'model', 'valueDomain')
    }

    def "value domain extra mappings mehtod #method maps and url #url maps to action #action"() {
        expect:
        assertRestForwardUrlMapping(method, url, controller: "valueDomain", action: action) {
            id = "1"
        }
        where:
        method      | action             | url
        "GET"       | "mappings"         | "/api/modelCatalogue/core/valueDomain/1/mapping"
        //"POST"      | "addMapping"       | "/api/modelCatalogue/core/valueDomain/1/mapping"
        //"DELETE"    | "removeMapping"    | "/api/modelCatalogue/core/valueDomain/1/mapping"
    }

    def "for method #method and url /api/modelCatalogue/core#url there should be no mappings found"() {
        when:
        assertRestForwardUrlMapping([controller: "foo", action: "bar"], method, "/api/modelCatalogue/core$url", {})

        then:
        thrown(AssertionError)

        where:
        method  | url
        "GET"   | "/fooBar"

    }


    private void assertRestForwardUrlMapping(assertions, String method, url, paramAssertions) {
        webRequest.currentRequest.method = method
        assertForwardUrlMapping(assertions, url, paramAssertions)
    }


    private generateAssertionsForCatalogueElementControllers(String... controllers ) {
        def ret = []
        for(controller in controllers) {
            ret.addAll generateRestAssertion(controller)
            ret.addAll generateRelationshipRestAssertions(controller)
        }
        ret
    }

    private generateRestAssertion(String controller) {
        [
      // method  | url              | controller | action            | paramsAssertions
        ["GET"   , "/$controller"   , controller , "index"           , {}            ],
        ["POST"  , "/$controller"   , controller , "save"            , {}            ],
        ["GET"   , "/$controller/1" , controller , "show"            , { id = "1" }  ],
        ["DELETE", "/$controller/1" , controller , "delete"          , { id = "1" }  ],
        ["PUT"   , "/$controller/1" , controller , "update"          , { id = "1" }  ]
        ]
    }

    private generateRelationshipRestAssertions(String controller) {
        [
      // method   | url                                       | controller | action            | paramsAssertions
        [ "GET"   , "/$controller/1/outgoing"                 , controller , "outgoing"        , { id = "1" }                          ],
        [ "GET"   , "/$controller/1/incoming"                 , controller , "incoming"        , { id = "1" }                          ],
        [ "GET"   , "/$controller/1/outgoing/relationship"    , controller , "outgoing"        , { id = "1" ; type = "relationship" }  ],
        [ "GET"   , "/$controller/1/incoming/relationship"    , controller , "incoming"        , { id = "1" ; type = "relationship" }  ],
        [ "POST"  , "/$controller/1/outgoing/relationship"    , controller , "addOutgoing"     , { id = "1" ; type = "relationship" }  ],
        [ "POST"  , "/$controller/1/incoming/relationship"    , controller , "addIncoming"     , { id = "1" ; type = "relationship" }  ],
        [ "DELETE", "/$controller/1/outgoing/relationship"    , controller , "removeOutgoing"  , { id = "1" ; type = "relationship" }  ],
        [ "DELETE", "/$controller/1/incoming/relationship"    , controller , "removeIncoming"  , { id = "1" ; type = "relationship" }  ]
        ]
    }


}
