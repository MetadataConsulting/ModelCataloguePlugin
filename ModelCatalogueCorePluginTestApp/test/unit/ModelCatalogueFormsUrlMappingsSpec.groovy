import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.forms.FormGeneratorController
import spock.lang.Specification
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(ModelCatalogueFormsUrlMappings)
@Mock(FormGeneratorController)
class ModelCatalogueFormsUrlMappingsSpec extends Specification {

    void "test MeasurementUnitUrlMappings GET Request mappings"() {
        given:
        request.method = 'get'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/forms/generate/$id', controller: 'formGenerator', action: 'generateForm')
        assertForwardUrlMapping('/api/modelCatalogue/core/forms/preview/$id', controller: 'formGenerator', action: 'previewForm')
    }
}
