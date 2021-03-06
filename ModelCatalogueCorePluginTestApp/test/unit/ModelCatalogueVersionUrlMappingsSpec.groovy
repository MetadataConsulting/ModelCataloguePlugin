import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.ModelCatalogueVersionController
import spock.lang.IgnoreIf
import spock.lang.Specification

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(ModelCatalogueVersionUrlMappings)
@Mock(ModelCatalogueVersionController)
class ModelCatalogueVersionUrlMappingsSpec extends Specification {

    void "test apiKey mappings"() {
        expect:
        assertForwardUrlMapping("/modelCatalogueVersion/index", controller: 'modelCatalogueVersion', action: 'index')
    }
}