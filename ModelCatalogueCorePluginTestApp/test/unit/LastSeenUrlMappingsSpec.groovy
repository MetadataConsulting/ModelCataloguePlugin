import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.LastSeenController
import spock.lang.IgnoreIf
import spock.lang.Specification

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(LastSeenUrlMappings)
@Mock(LastSeenController)
class LastSeenUrlMappingsSpec extends Specification {

    void "test lastSeen mappings"() {
        expect:
        assertForwardUrlMapping("/lastSeen/index", controller: 'lastSeen', action: 'index')
    }
}