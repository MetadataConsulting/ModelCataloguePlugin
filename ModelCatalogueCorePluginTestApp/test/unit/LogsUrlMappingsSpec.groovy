import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.AssetController
import org.modelcatalogue.core.LogsController
import spock.lang.IgnoreIf
import spock.lang.Specification

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(LogsUrlMappings)
@Mock(LogsController)
class LogsUrlMappingsSpec extends Specification {

    void "test apiKey mappings"() {
        expect:
        assertForwardUrlMapping("/logs/index", controller: 'logs', action: 'index')
    }
}
