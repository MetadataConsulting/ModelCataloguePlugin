import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.MeasurementUnitController
import spock.lang.Specification

@TestFor(MeasurementUnitUrlMappings)
@Mock(MeasurementUnitController)
class MeasurementUnitUrlMappingsSpec extends Specification {
    void "test MeasurementUnitUrlMappings POST Request mappings"() {
        given:
        request.method = 'post'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit', controller: 'measurementUnit', action: 'save')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/validate', controller: 'measurementUnit', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/validate', controller: 'measurementUnit', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/archive', controller: 'measurementUnit', action: 'archive')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/outgoing/$type', controller: 'measurementUnit', action: 'addOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/restore', controller: 'measurementUnit', action: 'restore')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/mapping/$destination', controller: 'measurementUnit', action: 'addMapping')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/incoming/$type', controller: 'measurementUnit', action: 'addIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/clone/$destinationDataModelId', controller: 'measurementUnit', action: 'cloneElement')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$source/merge/$destination', controller: 'measurementUnit', action: 'merge')
    }

    void "test MeasurementUnitUrlMappings GET Request mappings"() {
        given:
        request.method = 'get'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit', controller: 'measurementUnit', action: 'index')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/search/$search', controller: 'measurementUnit', action: 'search')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id', controller: 'measurementUnit', action: 'show')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/outgoing/search', controller: 'measurementUnit', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/outgoing/$type/search', controller: 'measurementUnit', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/outgoing/$type', controller: 'measurementUnit', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/incoming/search', controller: 'measurementUnit', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/incoming/$type/search', controller: 'measurementUnit', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/incoming/$type', controller: 'measurementUnit', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/incoming', controller: 'measurementUnit', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/outgoing', controller: 'measurementUnit', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/mapping', controller: 'measurementUnit', action: 'mappings')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/typeHierarchy', controller: 'measurementUnit', action: 'typeHierarchy')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/history', controller: 'measurementUnit', action: 'history')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/path', controller: 'measurementUnit', action: 'path')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/primitiveType', controller: 'measurementUnit', action: 'primitiveTypes')
    }

    void "test MeasurementUnitUrlMappings PUT Request mappings"() {
        given:
        request.method = 'put'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id', controller: 'measurementUnit', action: 'update')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/incoming/$type', controller: 'measurementUnit', action: 'reorderIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/outgoing/$type', controller: 'measurementUnit', action: 'reorderOutgoing')
    }

    void "test MeasurementUnitUrlMappings delete Request mappings"() {
        given:
        request.method = 'delete'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id', controller: 'measurementUnit', action: 'delete')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/mapping/$destination', controller: 'measurementUnit', action: 'removeMapping')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/incoming/$type', controller: 'measurementUnit', action: 'removeIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/measurementUnit/$id/outgoing/$type', controller: 'measurementUnit', action: 'removeOutgoing')
    }
}
