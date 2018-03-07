import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.DataModelController
import org.modelcatalogue.core.CatalogueController
import spock.lang.Specification
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(DataModelUrlMappings)
@Mock([DataModelController, CatalogueController])
class DataModelUrlMappingsSpec extends Specification {
    void "test DataModelUrlMappings POST Request mappings"() {
        given:
        request.method = 'post'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel', controller: 'dataModel', action: 'save')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/validate', controller: 'dataModel', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/validate', controller: 'dataModel', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/outgoing/$type', controller: 'dataModel', action: 'addOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/reindex', controller: 'dataModel', action: 'reindex')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/incoming/$type', controller: 'dataModel', action: 'addIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/newVersion', controller: 'dataModel', action: 'newVersion')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/mapping/$destination', controller: 'dataModel', action: 'addMapping')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/archive', controller: 'dataModel', action: 'archive')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/restore', controller: 'dataModel', action: 'restore')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/finalize', controller: 'dataModel', action: 'finalizeElement')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/clone/$destinationDataModelId', controller: 'dataModel', action: 'cloneElement')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$source/merge/$destination', controller: 'dataModel', action: 'merge')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/preload', controller: 'catalogue', action: 'importFromUrl')
    }

    void "test DataModelUrlMappings GET Request mappings"() {
        given:
        request.method = 'get'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel', controller: 'dataModel', action: 'index')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/search/$search', controller: 'dataModel', action: 'search')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id', controller: 'dataModel', action: 'show')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/outgoing/search', controller: 'dataModel', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/outgoing/$type/search', controller: 'dataModel', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/outgoing/$type', controller: 'dataModel', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/incoming/search', controller: 'dataModel', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/incoming/$type/search', controller: 'dataModel', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/incoming/$type', controller: 'dataModel', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/incoming', controller: 'dataModel', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/outgoing', controller: 'dataModel', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/mapping', controller: 'dataModel', action: 'mappings')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/typeHierarchy', controller: 'dataModel', action: 'typeHierarchy')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/history', controller: 'dataModel', action: 'history')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/path', controller: 'dataModel', action: 'path')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/preload', controller: 'catalogue', action: 'dataModelsForPreload')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/containsOrImports/$other', controller: 'dataModel', action: 'containsOrImports')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/content', controller: 'dataModel', action: 'content')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/inventorySpreadsheet', controller: 'dataModel', action: 'inventorySpreadsheet')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/gridSpreadsheet', controller: 'dataModel', action: 'gridSpreadsheet')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/excelExporterSpreadsheet', controller: 'dataModel', action: 'excelExporterSpreadsheet')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/inventoryDoc', controller: 'dataModel', action: 'inventoryDoc')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/dependents', controller: 'dataModel', action: 'dependents')
    }

    void "test DataModelUrlMappings PUT Request mappings"() {
        given:
        request.method = 'put'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id', controller: 'dataModel', action: 'update')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/outgoing/$type', controller: 'dataModel', action: 'reorderOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/incoming/$type', controller: 'dataModel', action: 'reorderIncoming')
    }

    void "test DataModelUrlMappings delete Request mappings"() {
        given:
        request.method = 'delete'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id', controller: 'dataModel', action: 'delete')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/outgoing/$type', controller: 'dataModel', action: 'removeOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/incoming/$type', controller: 'dataModel', action: 'removeIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataModel/$id/mapping/$destination', controller: 'dataModel', action: 'removeMapping')
    }
}
