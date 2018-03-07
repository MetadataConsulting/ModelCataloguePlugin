import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.ApiKeyController
import spock.lang.IgnoreIf
import spock.lang.Specification

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(ApiKeyUrlMappings)
@Mock(ApiKeyController)
class ApiKeyUrlMappingsSpec extends Specification {

    void "test apiKey mappings"() {
        expect:
        assertForwardUrlMapping("/apiKey/index", controller: 'apiKey', action: 'index')
    }
}