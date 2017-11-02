
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import junit.framework.AssertionFailedError
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import org.codehaus.groovy.grails.web.mapping.UrlMappingsHolder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.actions.BatchController
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

@Ignore
@TestFor(ModelCatalogueCorePluginUrlMappings)
@Mock([
    DataElementController,
    DataTypeController,
    EnumeratedTypeController,
    MeasurementUnitController,
    DataClassController,
    SearchController,
    BatchController
])
@Unroll
class ModelCatalogueCorePluginUrlMappingsSpec extends Specification {

    private assertionKeys = ["controller", "action", "view"]

    def "for method #method and url /api/modelCatalogue/core#url expect controller #controller and action #action with #paramsAssertions"() {
        expect:
        assertRestForwardUrlMapping(method, "/api/modelCatalogue/core$url".toString(), controller: controller, action: action, paramsAssertions)

        where:
        [method, url, controller, action, paramsAssertions] << generateAssertionsForCatalogueElementControllers('dataElement', 'dataType', 'enumeratedType', 'measurementUnit', 'dataClass')
    }

    def "value domain extra mappings mehtod #method maps and url #url maps to action #action"() {
        expect:
        assertRestForwardUrlMapping(method, url.toString(), controller: "dataType", action: action, paramsToCheck)
        where:
        method      | action             | url                                                  | paramsToCheck
        "GET"       | "mappings"         | "/api/modelCatalogue/core/dataType/1/mapping"     | {id = "1"}
        "POST"      | "addMapping"       | "/api/modelCatalogue/core/dataType/1/mapping/2"   | {id = "1" ; destination = "2"}
        "DELETE"    | "removeMapping"    | "/api/modelCatalogue/core/dataType/1/mapping/2"   | {id = "1" ; destination = "2"}
    }

    def "for method #method and url /api/modelCatalogue/core#url there should be no mappings found"() {
        when:
        assertRestForwardUrlMapping([controller: "foo", action: "bar"], method, "/api/modelCatalogue/core$url".toString(), {})

        then:
        thrown(AssertionError)

        where:
        method  | url
        "GET"   | "/fooBar"

    }

    def "search controller url mapping"() {
        expect:
        assertRestForwardUrlMapping("GET", "/api/modelCatalogue/core/search/author", controller: "search", action: "index", { search = "author"} )
        assertRestForwardUrlMapping("GET", "/api/modelCatalogue/core/search", controller: "search", action: "index", {} )

    }


    @Ignore
    def "batch controller url mapping"() {
        expect:
        assertRestForwardUrlMapping("GET", "/api/modelCatalogue/core/batch/1/actions/pending", controller: "batch", action: "listActions", {
            id = '1'
            state = 'pending'
        })
    }



    private void assertRestForwardUrlMapping(Map assertions, String method, String url, Closure paramAssertions) {
        UrlMappingsHolder mappingsHolder = applicationContext.getBean("grailsUrlMappingsHolder", UrlMappingsHolder)
        if (assertions.action && !assertions.controller) {
            throw new AssertionFailedError("Cannot assert action for url mapping without asserting controller")
        }

        if (assertions.controller) assertController(assertions.controller, url)
        if (assertions.action) assertAction(assertions.controller, assertions.action, url)
        if (assertions.view) assertView(assertions.controller, assertions.view, url)

        def mappingInfos
        if (url instanceof Integer) {
            mappingInfos = []
            def mapping = mappingsHolder.matchStatusCode(url)
            if (mapping) mappingInfos << mapping
        }
        else {
            mappingInfos = mappingsHolder.matchAll(url, method)
        }

        if (mappingInfos.size() == 0) throw new AssertionFailedError("url '$url' did not match any mappings")

        def mappingMatched = mappingInfos.any {mapping ->
            mapping.configure(webRequest)
            for (key in assertionKeys) {
                if (assertions.containsKey(key)) {
                    def expected = assertions[key]
                    def actual = mapping."${key}Name"

                    switch (key) {
                        case "controller":
                            if (actual && !getControllerClass(actual)) return false
                            break
                        case "view":
                            if (actual[0] == "/") actual = actual.substring(1)
                            if (expected[0] == "/") expected = expected.substring(1)
                            break
                        case "action":
                            if (key == "action" && actual == null) {
                                final controllerClass = getControllerClass(assertions.controller)
                                actual = controllerClass?.defaultAction
                            }
                            break
                    }

                    assert expected == actual
                }
            }
            if (paramAssertions) {
                def params = [:]
                paramAssertions.delegate = params
                paramAssertions.resolveStrategy = Closure.DELEGATE_ONLY
                paramAssertions.call()
                params.each {name, value ->
                    assert value == mapping.params[name]
                }
            }
            return true
        }

        if (!mappingMatched) throw new IllegalArgumentException("url '$url' did not match any mappings")
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
      // method  | url                                                      | controller | action               | paramsAssertions
        ["GET"   , "/$controller"                                           , controller , "index"              , {}            ],
        ["POST"  , "/$controller"                                           , controller , "save"               , {}            ],
        ["GET"   , "/$controller/1"                                         , controller , "show"               , { id = "1" }  ],
        ["DELETE", "/$controller/1"                                         , controller , "delete"             , { id = "1" }  ],
        ["PUT"   , "/$controller/1"                                         , controller , "update"             , { id = "1" }  ],
        ["GET"   , "/$controller/search/author"                             , controller , "search"             , { search = "author"} ],
        ["GET"   , "/$controller/search"                                    , controller , "search"             , {}            ],
        ["GET"   , "/$controller/1/incoming/search"                         , controller , "searchIncoming"     , { id = "1" }  ],
        ["GET"   , "/$controller/1/outgoing/search"                         , controller , "searchOutgoing"     , { id = "1" }  ],
        ["GET"   , "/$controller/1/incoming/relationship/search"            , controller , "searchIncoming"     , { id = "1" ; type = "relationship" }  ],
        ["GET"   , "/$controller/1/outgoing/relationship/search"            , controller , "searchOutgoing"     , { id = "1" ; type = "relationship" }  ],
        ]
    }

    private generateRelationshipRestAssertions(String controller) {
        [
      // method   | url                                       | controller | action            | paramsAssertions
        [ "POST"  , "/$controller/validate"                   , controller , "validate"        , { }                                   ],
        [ "POST"  , "/$controller/1/validate"                 , controller , "validate"        , { id = "1" }                          ],
        [ "GET"   , "/$controller/1/outgoing"                 , controller , "outgoing"        , { id = "1" }                          ],
        [ "GET"   , "/$controller/1/incoming"                 , controller , "incoming"        , { id = "1" }                          ],
        [ "GET"   , "/$controller/1/outgoing/relationship"    , controller , "outgoing"        , { id = "1" ; type = "relationship" }  ],
        [ "GET"   , "/$controller/1/incoming/relationship"    , controller , "incoming"        , { id = "1" ; type = "relationship" }  ],
        [ "POST"  , "/$controller/1/outgoing/relationship"    , controller , "addOutgoing"     , { id = "1" ; type = "relationship" }  ],
        [ "POST"  , "/$controller/1/incoming/relationship"    , controller , "addIncoming"     , { id = "1" ; type = "relationship" }  ],
        [ "DELETE", "/$controller/1/outgoing/relationship"    , controller , "removeOutgoing"  , { id = "1" ; type = "relationship" }  ],
        [ "DELETE", "/$controller/1/incoming/relationship"    , controller , "removeIncoming"  , { id = "1" ; type = "relationship" }  ],
        ]
    }

    private GrailsControllerClass getControllerClass(controller) {
        return grailsApplication.getArtefactByLogicalPropertyName(org.codehaus.groovy.grails.commons.ControllerArtefactHandler.TYPE, controller)
    }


}
