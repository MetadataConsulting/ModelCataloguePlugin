import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.CsvTransformationController
import spock.lang.Specification
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(CsvTransformationUrlMappings)
@Mock(CsvTransformationController)
class CsvTransformationUrlMappingsSpec extends Specification {
    void "test CsvTransformationUrlMappings POST Request mappings"() {
        given:
        request.method = 'post'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/csvTransformation', controller: 'csvTransformation', action: 'save')
        assertForwardUrlMapping('/api/modelCatalogue/core/csvTransformation/$id/validate', controller: 'csvTransformation', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/csvTransformation/validate', controller: 'csvTransformation', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/csvTransformation/$id/transform', controller: 'csvTransformation', action: 'transform')
    }

    void "test CsvTransformationUrlMappings GET Request mappings"() {
        given:
        request.method = 'get'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/csvTransformation', controller: 'csvTransformation', action: 'index')
        assertForwardUrlMapping('/api/modelCatalogue/core/csvTransformation/search/$search', controller: 'csvTransformation', action: 'search')
        assertForwardUrlMapping('/api/modelCatalogue/core/csvTransformation/$id', controller: 'csvTransformation', action: 'show')



    }

    void "test CsvTransformationUrlMappings PUT Request mappings"() {
        given:
        request.method = 'put'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/csvTransformation/$id', controller: 'csvTransformation', action: 'update')
    }

    void "test CsvTransformationUrlMappings delete Request mappings"() {
        given:
        request.method = 'delete'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/csvTransformation/$id', controller: 'csvTransformation', action: 'delete')
    }
}
