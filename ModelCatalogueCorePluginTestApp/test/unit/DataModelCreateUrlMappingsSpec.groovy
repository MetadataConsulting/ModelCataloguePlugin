import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.DataModelCreateController
import spock.lang.IgnoreIf
import spock.lang.Specification

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(DataModelCreateUrlMappings)
@Mock(DataModelCreateController)
class DataModelCreateUrlMappingsSpec extends Specification {

    void "test apiKey mappings"() {
        expect:
        assertForwardUrlMapping("/dataModel/create", controller: 'dataModelCreate', action: 'create')
    }
}