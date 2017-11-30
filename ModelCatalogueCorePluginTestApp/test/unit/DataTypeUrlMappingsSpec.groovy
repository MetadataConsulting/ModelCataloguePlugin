import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.DataTypeController
import spock.lang.Specification

@TestFor(DataTypeUrlMappings)
@Mock(DataTypeController)
class DataTypeUrlMappingsSpec extends Specification {
    void "test DataTypeUrlMappings POST Request mappings"() {
        given:
        request.method = 'post'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType', controller: 'dataType', action: 'save')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/validate', controller: 'dataType', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/validate', controller: 'dataType', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/outgoing/$type', controller: 'dataType', action: 'addOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/incoming/$type', controller: 'dataType', action: 'addIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/mapping/$destination', controller: 'dataType', action: 'addMapping')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/archive', controller: 'dataType', action: 'archive')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/restore', controller: 'dataType', action: 'restore')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/clone/$destinationDataModelId', controller: 'dataType', action: 'cloneElement')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$source/merge/$destination', controller: 'dataType', action: 'merge')
    }

    void "test DataTypeUrlMappings GET Request mappings"() {
        given:
        request.method = 'get'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType', controller: 'dataType', action: 'index')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/search/$search', controller: 'dataType', action: 'search')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id', controller: 'dataType', action: 'show')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/outgoing/search', controller: 'dataType', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/outgoing/$type/search', controller: 'dataType', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/outgoing/$type', controller: 'dataType', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/incoming/search', controller: 'dataType', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/incoming/$type/search', controller: 'dataType', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/incoming/$type', controller: 'dataType', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/incoming', controller: 'dataType', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/outgoing', controller: 'dataType', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/mapping', controller: 'dataType', action: 'mappings')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/typeHierarchy', controller: 'dataType', action: 'typeHierarchy')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/history', controller: 'dataType', action: 'history')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/path', controller: 'dataType', action: 'path')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/dataElement', controller: 'dataType', action: 'dataElements')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/convert/$destination', controller: 'dataType', action: 'convert')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/validateValue', controller: 'dataType', action: 'validateValue')
    }

    void "test DataTypeUrlMappings PUT Request mappings"() {
        given:
        request.method = 'put'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id', controller: 'dataType', action: 'update')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/outgoing/$type', controller: 'dataType', action: 'reorderOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/incoming/$type', controller: 'dataType', action: 'reorderIncoming')
    }

    void "test DataTypeUrlMappings delete Request mappings"() {
        given:
        request.method = 'delete'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id', controller: 'dataType', action: 'delete')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/outgoing/$type', controller: 'dataType', action: 'removeOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/incoming/$type', controller: 'dataType', action: 'removeIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataType/$id/mapping/$destination', controller: 'dataType', action: 'removeMapping')
    }
}
