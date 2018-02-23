import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.actions.BatchController
import spock.lang.Specification
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(BatchUrlMappings)
@Mock(BatchController)
class BatchUrlMappingsSpec extends Specification {

    void "test batch mappings"() {
        expect:
        assertForwardUrlMapping("/batch/all", controller: 'batch', action: 'all')
        assertForwardUrlMapping("/batch/create", controller: 'batch', action: 'create')
        //assertForwardUrlMapping("/batch/generateSuggestions", controller: 'batch', action: 'generateSuggestions')
    }
}