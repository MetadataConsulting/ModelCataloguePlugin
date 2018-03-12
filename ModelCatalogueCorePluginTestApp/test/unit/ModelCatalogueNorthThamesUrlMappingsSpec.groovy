import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.norththames.NorthThamesController
import spock.lang.Specification
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(ModelCatalogueNorthThamesUrlMappings)
@Mock(NorthThamesController)
class ModelCatalogueNorthThamesUrlMappingsSpec extends Specification {

    void "test ModelCatalogueNorthThamesUrlMappings GET Request mappings"() {
        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/northThames/northThamesGridHierarchyMappingSummaryReport/$id', controller: 'northThames', action: 'northThamesGridHierarchyMappingSummaryReport')
        assertForwardUrlMapping('/api/modelCatalogue/core/northThames/northThamesMappingReport/$id', controller: 'northThames', action: 'northThamesMappingReport')
    }
}
