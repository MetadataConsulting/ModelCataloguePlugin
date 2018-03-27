import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.AssetController
import spock.lang.IgnoreIf
import spock.lang.Specification

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(AssetUrlMappings)
@Mock(AssetController)
class AssetUrlMappingsSpec extends Specification {

    void "test apiKey mappings"() {
        expect:
        assertForwardUrlMapping("/api/modelCatalogue/core/asset", controller: 'asset', action: 'index')
    }
}
