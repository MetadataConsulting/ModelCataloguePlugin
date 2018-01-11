import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.norththames.NorthThamesController
import spock.lang.Specification

@TestFor(ModelCatalogueNorthThamesUrlMappings)
@Mock(NorthThamesController)
class ModelCatalogueNorthThamesUrlMappingsSpec extends Specification {

    void "test UserUrlMappings GET Request mappings"() {
        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/northThames/northThamesGridHierarchyMappingSummaryReport/$id', controller: 'northThames', action: 'northThamesMappingReport')
    }
}
