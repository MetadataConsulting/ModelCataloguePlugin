import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.DataArchitectController
import org.modelcatalogue.core.DataImportController
import spock.lang.Specification
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(DataArchitectUrlMappings)
@Mock([DataArchitectController, DataImportController])
class DataArchitectUrlMappingsSpec extends Specification {
    void "test DataArchitectUrlMappings POST Request mappings"() {
        given:
        request.method = 'post'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataArchitect/elementsFromCSV', controller: "dataArchitect", action: "elementsFromCSV")
        assertForwardUrlMapping('/api/modelCatalogue/core/dataArchitect/modelsFromCSV', controller: "dataArchitect", action: "modelsFromCSV")
        assertForwardUrlMapping('/api/modelCatalogue/core/dataArchitect/generateSuggestions', controller: "dataArchitect", action: "generateSuggestions")
        assertForwardUrlMapping('/api/modelCatalogue/core/dataArchitect/deleteSuggestions', controller: "dataArchitect", action: "deleteSuggestions")
        assertForwardUrlMapping('/api/modelCatalogue/core/dataArchitect/imports/upload', controller: "dataImport", action: 'upload')
    }

    void "test DataArchitectUrlMappings GET Request mappings"() {
        given:
        request.method = 'get'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataArchitect/metadataKeyCheck/$key', controller: "dataArchitect", action: 'metadataKeyCheck')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataArchitect/getSubModelElements/$modelId', controller: "dataArchitect", action: 'getSubModelElements')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataArchitect/findRelationsByMetadataKeys/$key', controller: "dataArchitect", action: 'findRelationsByMetadataKeys')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataArchitect/suggestionsNames', controller: "dataArchitect", action: "suggestionsNames")
    }
}
