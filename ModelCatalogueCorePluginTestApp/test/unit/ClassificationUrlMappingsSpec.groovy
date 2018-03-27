import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.CatalogueController
import org.modelcatalogue.core.DataModelController
import spock.lang.Specification
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(ClassificationUrlMappings)
@Mock([DataModelController, CatalogueController])
class ClassificationUrlMappingsSpec extends Specification {
    void "test ClassificationUrlMappings POST Request mappings"() {
        given:
        request.method = 'post'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/classification', controller: 'dataModel', action: 'save')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/validate', controller: 'dataModel', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/validate', controller: 'dataModel', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/outgoing/$type', controller: 'dataModel', action: 'addOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/incoming/$type', controller: 'dataModel', action: 'addIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/mapping/$destination', controller: 'dataModel', action: 'addMapping')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/archive', controller: 'dataModel', action: 'archive')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/restore', controller: 'dataModel', action: 'restore')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/clone/$destinationDataModelId', controller: 'dataModel', action: 'cloneElement')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$source/merge/$destination', controller: 'dataModel', action: 'merge')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/preload', controller: 'catalogue', action: 'importFromUrl')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/newVersion', controller: 'dataModel', action: 'newVersion')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/reindex', controller: 'dataModel', action: 'reindex')
    }

    void "test ClassificationUrlMappings GET Request mappings"() {
        given:
        request.method = 'get'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/incoming', controller: 'dataModel', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/outgoing', controller: 'dataModel', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/preload', controller: 'catalogue', action: 'dataModelsForPreload')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/inventorySpreadsheet', controller: 'dataModel', action: 'inventorySpreadsheet')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/incoming/search', controller: 'dataModel', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/incoming/$type/search', controller: 'dataModel', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/incoming/$type', controller: 'dataModel', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification', controller: 'dataModel', action: 'index')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/search/$search', controller: 'dataModel', action: 'search')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id', controller: 'dataModel', action: 'show')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/outgoing/search', controller: 'dataModel', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/outgoing/$type/search', controller: 'dataModel', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/outgoing/$type', controller: 'dataModel', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/gridSpreadsheet', controller: 'dataModel', action: 'gridSpreadsheet')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/excelExporterSpreadsheet', controller: 'dataModel', action: 'excelExporterSpreadsheet')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/inventoryDoc', controller: 'dataModel', action: 'inventoryDoc')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/dependents', controller: 'dataModel', action: 'dependents')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/containsOrImports/$other', controller: 'dataModel', action: 'containsOrImports')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/content', controller: 'dataModel', action: 'content')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/mapping', controller: 'dataModel', action: 'mappings')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/typeHierarchy', controller: 'dataModel', action: 'typeHierarchy')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/history', controller: 'dataModel', action: 'history')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/path', controller: 'dataModel', action: 'path')
    }

    void "test ClassificationUrlMappings PUT Request mappings"() {
        given:
        request.method = 'put'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id', controller: 'dataModel', action: 'update')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/outgoing/$type', controller: 'dataModel', action: 'reorderOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/incoming/$type', controller: 'dataModel', action: 'reorderIncoming')
    }

    void "test ClassificationUrlMappings delete Request mappings"() {
        given:
        request.method = 'delete'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id', controller: 'dataModel', action: 'delete')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/outgoing/$type', controller: 'dataModel', action: 'removeOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/incoming/$type', controller: 'dataModel', action: 'removeIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/classification/$id/mapping/$destination', controller: 'dataModel', action: 'removeMapping')
    }
}
