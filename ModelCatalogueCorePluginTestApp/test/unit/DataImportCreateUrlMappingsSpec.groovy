import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.ApiKeyController
import org.modelcatalogue.core.DataImportCreateController
import org.springframework.http.HttpMethod
import spock.lang.IgnoreIf
import spock.lang.Specification

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(DataImportCreateUrlMappings)
@Mock(DataImportCreateController)
class DataImportCreateUrlMappingsSpec extends Specification {

    void "test apiKey mappings"() {
        expect:
        assertForwardUrlMapping("/dataImport/obo", controller: 'dataImportCreate', action: 'importObo')
        assertForwardUrlMapping("/dataImport/dsl", controller: 'dataImportCreate', action: 'importModelCatalogueDSL')
        assertForwardUrlMapping("/dataImport/excel", controller: 'dataImportCreate', action: 'importExcel')
        assertForwardUrlMapping("/dataImport/xml", controller: 'dataImportCreate', action: 'importXml')
    }
}