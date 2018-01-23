import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.mappingsuggestions.MappingSuggestionsController
import spock.lang.Specification

@TestFor(MappingSuggestionsUrlMappings)
@Mock(MappingSuggestionsController)
class MappingSuggestionsUrlMappingsSpec extends Specification {

    void "test MappingSuggestions mappings"() {
        expect:
        assertForwardUrlMapping("/mappingsuggestions", controller: 'mappingSuggestions')
        assertForwardUrlMapping("/mappingsuggestions/reject", controller: 'mappingSuggestions', action: 'reject')
        assertForwardUrlMapping("/mappingsuggestions/approve", controller: 'mappingSuggestions', action: 'approve')
    }
}
