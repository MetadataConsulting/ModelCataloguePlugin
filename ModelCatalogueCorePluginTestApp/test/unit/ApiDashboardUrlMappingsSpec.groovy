import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.ApiDashboardController
import spock.lang.IgnoreIf
import spock.lang.Specification

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(ApiDashboardUrlMappings)
@Mock(ApiDashboardController)
class ApiDashboardUrlMappingsSpec extends Specification {

    void "test apiKey mappings"() {
        expect:
        assertForwardUrlMapping("/api/dashboard/dataModels", controller: 'apiDashboard', action: 'dataModels')
    }
}