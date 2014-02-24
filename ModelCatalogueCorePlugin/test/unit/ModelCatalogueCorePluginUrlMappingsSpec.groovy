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
        method  | url                                           | controller        | action            | paramsAssertions
        "GET"   | "/measurementUnit"                            | "measurementUnit" | "index"           | {}
        "POST"  | "/measurementUnit"                            | "measurementUnit" | "save"            | {}
        "GET"   | "/measurementUnit/1"                          | "measurementUnit" | "show"            | { id = "1" }
        "DELETE"| "/measurementUnit/1"                          | "measurementUnit" | "delete"          | { id = "1" }
        "PUT"   | "/measurementUnit/1"                          | "measurementUnit" | "update"          | { id = "1" }
        "GET"   | "/measurementUnit/1/outgoing"                 | "measurementUnit" | "outgoing"        | { id = "1" }
        "GET"   | "/measurementUnit/1/incoming"                 | "measurementUnit" | "incoming"        | { id = "1" }
        "GET"   | "/measurementUnit/1/outgoing/relationship"    | "measurementUnit" | "outgoing"        | { id = "1" ; type = "relationship" }
        "GET"   | "/measurementUnit/1/incoming/relationship"    | "measurementUnit" | "incoming"        | { id = "1" ; type = "relationship" }
        "POST"  | "/measurementUnit/1/outgoing/relationship"    | "measurementUnit" | "addOutgoing"     | { id = "1" ; type = "relationship" }
        "POST"  | "/measurementUnit/1/incoming/relationship"    | "measurementUnit" | "addIncoming"     | { id = "1" ; type = "relationship" }
        "DELETE"| "/measurementUnit/1/outgoing/relationship"    | "measurementUnit" | "removeOutgoing"  | { id = "1" ; type = "relationship" }
        "DELETE"| "/measurementUnit/1/incoming/relationship"    | "measurementUnit" | "removeIncoming"  | { id = "1" ; type = "relationship" }
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

}
