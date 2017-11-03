import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.SearchController
import org.modelcatalogue.core.RelationshipController
import org.modelcatalogue.core.CatalogueController
import org.modelcatalogue.core.logging.LoggingController
import org.modelcatalogue.core.DataImportController
import org.modelcatalogue.core.forms.FormGeneratorController
import spock.lang.Specification

@TestFor(ModelCatalogueCorePluginUrlMappings)
@Mock([SearchController,RelationshipController, CatalogueController, LoggingController, DataImportController, FormGeneratorController])
class ModelCatalogueCorePluginUrlMappingsSpec extends Specification {
    void "test ModelCatalogueCorePluginUrlMappings POST Request mappings"() {
        given:
        request.method = 'post'

        expect:
        assertForwardUrlMapping('/catalogue/upload', controller: "dataImport", action: 'upload')
        assertForwardUrlMapping('/api/modelCatalogue/core/search/reindex',controller:"search", action : 'reindex')
        assertForwardUrlMapping('/api/modelCatalogue/core/relationship/$id/restore',controller:"relationship", action : 'restore')
    }

    void "test ModelCatalogueCorePluginUrlMappings GET Request mappings"() {
        given:
        request.method = 'get'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/forms/generate/$id',controller: 'formGenerator', action: 'generateForm')
        assertForwardUrlMapping('/api/modelCatalogue/core/forms/preview/$id',controller: 'formGenerator', action: 'previewForm')
        assertForwardUrlMapping('/catalogue/ext/$key/$value',controller: 'catalogue', action: 'ext')
        assertForwardUrlMapping('/catalogue/ext/$key/$value/export',controller: 'catalogue', action: 'ext')
        assertForwardUrlMapping('/catalogue/$resource/$id',controller: 'catalogue', action: 'xref')
        assertForwardUrlMapping('/catalogue/$resource/$id/export',controller: 'catalogue', action: 'xref')
        assertForwardUrlMapping('/api/modelCatalogue/core/feedback', controller: 'catalogue', action: 'feedbacks')
        assertForwardUrlMapping('/api/modelCatalogue/core/feedback/$key', controller: 'catalogue', action: 'feedback')
        assertForwardUrlMapping('/api/modelCatalogue/core/logs', controller: 'logging', action: 'logsToAssets')
        assertForwardUrlMapping('/', view:"index")
        assertForwardUrlMapping('/load', view:"load")
        assertForwardUrlMapping('/api/modelCatalogue/core/search/$search?',controller:"search", action : 'index')
    }
}
