import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.DataModelPolicyController
import spock.lang.Specification

@TestFor(DataModelPolicyUrlMappings)
@Mock(DataModelPolicyController)
class DataModelPolicyUrlMappingsSpec extends Specification {
    void "test DataModelPolicyUrlMappings POST Request mappings"() {
        given:
        request.method = 'post'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModelPolicy', controller: 'dataModelPolicy', action: 'save')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModelPolicy/$id/validate', controller: 'dataModelPolicy', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModelPolicy/validate', controller: 'dataModelPolicy', action: 'validate')
    }

    void "test DataModelPolicyUrlMappings GET Request mappings"() {
        given:
        request.method = 'get'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModelPolicy', controller: 'dataModelPolicy', action: 'index')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModelPolicy/search/$search', controller: 'dataModelPolicy', action: 'search')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModelPolicy/$id', controller: 'dataModelPolicy', action: 'show')
    }

    void "test DataModelPolicyUrlMappings PUT Request mappings"() {
        given:
        request.method = 'put'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModelPolicy/$id', controller: 'dataModelPolicy', action: 'update')
    }

    void "test DataModelPolicyUrlMappings delete Request mappings"() {
        given:
        request.method = 'delete'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModelPolicy/$id', controller: 'dataModelPolicy', action: 'delete')
    }
}
