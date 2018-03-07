import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.ReindexCatalogueController
import spock.lang.IgnoreIf
import spock.lang.Specification

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(ReindexCatalogueUrlMappings)
@Mock(ReindexCatalogueController)
class ReindexCatalogueUrlMappingsSpec extends Specification {

    void "test apiKey mappings"() {
        expect:
        assertForwardUrlMapping("/reindexCatalogue/index", controller: 'reindexCatalogue', action: 'index')
    }
}
