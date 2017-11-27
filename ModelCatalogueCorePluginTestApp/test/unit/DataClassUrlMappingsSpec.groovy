import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.DataClassController
import spock.lang.Specification

@TestFor(DataClassUrlMappings)
@Mock(DataClassController)
class DataClassUrlMappingsSpec extends Specification {
    void "test DataClassUrlMappings POST Request mappings"() {
        given:
        request.method = 'post'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass', controller: 'dataClass', action: 'save')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/validate', controller: 'dataClass', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/validate', controller: 'dataClass', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/outgoing/$type', controller: 'dataClass', action: 'addOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/archive', controller: 'dataClass', action: 'archive')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/restore', controller: 'dataClass', action: 'restore')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/clone/$destinationDataModelId', controller: 'dataClass', action: 'cloneElement')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$source/merge/$destination', controller: 'dataClass', action: 'merge')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/mapping/$destination', controller: 'dataClass', action: 'addMapping')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/mapping/$destination', controller: 'dataClass', action: 'addMapping')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/incoming/$type', controller: 'dataClass', action: 'addIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/outgoing/$type', controller: 'dataClass', action: 'addOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/incoming/$type', controller: 'dataClass', action: 'addIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/validate', controller: 'dataClass', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/validate', controller: 'dataClass', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/model', controller: 'dataClass', action: 'save')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/archive', controller: 'dataClass', action: 'archive')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/restore', controller: 'dataClass', action: 'restore')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/clone/$destinationDataModelId', controller: 'dataClass', action: 'cloneElement')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$source/merge/$destination', controller: 'dataClass', action: 'merge')
    }

    void "test DataClassUrlMappings GET Request mappings"() {
        given:
        request.method = 'get'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass', controller: 'dataClass', action: 'index')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/search/$search', controller: 'dataClass', action: 'search')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id', controller: 'dataClass', action: 'show')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/outgoing/search', controller: 'dataClass', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/outgoing/$type/search', controller: 'dataClass', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/outgoing/$type', controller: 'dataClass', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/incoming/search', controller: 'dataClass', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/incoming/$type/search', controller: 'dataClass', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/incoming/$type', controller: 'dataClass', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/incoming', controller: 'dataClass', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/outgoing', controller: 'dataClass', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/mapping', controller: 'dataClass', action: 'mappings')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/typeHierarchy', controller: 'dataClass', action: 'typeHierarchy')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/history', controller: 'dataClass', action: 'history')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/path', controller: 'dataClass', action: 'path')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/inventoryDoc', controller: 'dataClass', action: 'inventoryDoc')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/classificationChangelog', controller: 'dataClass', action: 'changelogDoc')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/inventorySpreadsheet', controller: 'dataClass', action: 'inventorySpreadsheet')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/referenceType', controller: 'dataClass', action: 'referenceTypes')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/content', controller: 'dataClass', action: 'content')
        assertForwardUrlMapping('/api/modelCatalogue/core/model', controller: 'dataClass', action: 'index')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/search/$search', controller: 'dataClass', action: 'search')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id', controller: 'dataClass', action: 'show')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/outgoing/search', controller: 'dataClass', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/outgoing/$type/search', controller: 'dataClass', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/outgoing/$type', controller: 'dataClass', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/incoming/search', controller: 'dataClass', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/incoming/$type/search', controller: 'dataClass', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/incoming/$type', controller: 'dataClass', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/incoming', controller: 'dataClass', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/outgoing', controller: 'dataClass', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/mapping', controller: 'dataClass', action: 'mappings')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/typeHierarchy', controller: 'dataClass', action: 'typeHierarchy')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/history', controller: 'dataClass', action: 'history')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/path', controller: 'dataClass', action: 'path')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/inventoryDoc', controller: 'dataClass', action: 'inventoryDoc')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/classificationChangelog', controller: 'dataClass', action: 'changelogDoc')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/inventorySpreadsheet', controller: 'dataClass', action: 'inventorySpreadsheet')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/referenceType', controller: 'dataClass', action: 'referenceTypes')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/content', controller: 'dataClass', action: 'content')
    }

    void "test DataClassUrlMappings PUT Request mappings"() {
        given:
        request.method = 'put'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id', controller: 'dataClass', action: 'update')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/incoming/$type', controller: 'dataClass', action: 'reorderIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/outgoing/$type', controller: 'dataClass', action: 'reorderOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/outgoing/$type', controller: 'dataClass', action: 'reorderOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id', controller: 'dataClass', action: 'update')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/incoming/$type', controller: 'dataClass', action: 'reorderIncoming')
    }

    void "test DataClassUrlMappings delete Request mappings"() {
        given:
        request.method = 'delete'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/mapping/$destination', controller: 'dataClass', action: 'removeMapping')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id', controller: 'dataClass', action: 'delete')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/mapping/$destination', controller: 'dataClass', action: 'removeMapping')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/incoming/$type', controller: 'dataClass', action: 'removeIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/incoming/$type', controller: 'dataClass', action: 'removeIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id/outgoing/$type', controller: 'dataClass', action: 'removeOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataClass/$id/outgoing/$type', controller: 'dataClass', action: 'removeOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/model/$id', controller: 'dataClass', action: 'delete')
    }
}
