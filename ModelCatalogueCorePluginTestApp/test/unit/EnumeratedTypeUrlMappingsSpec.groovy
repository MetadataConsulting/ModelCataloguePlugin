import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.EnumeratedTypeController
import spock.lang.Specification
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(EnumeratedTypeUrlMappings)
@Mock(EnumeratedTypeController)
class EnumeratedTypeUrlMappingsSpec extends Specification {
    void "test EnumeratedTypeUrlMappings POST Request mappings"() {
        given:
        request.method = 'post'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/archive', controller: 'enumeratedType', action: 'archive')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/restore', controller: 'enumeratedType', action: 'restore')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/clone/$destinationDataModelId', controller: 'enumeratedType', action: 'cloneElement')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$source/merge/$destination', controller: 'enumeratedType', action: 'merge')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType', controller: 'enumeratedType', action: 'save')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/validate', controller: 'enumeratedType', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/setDeprecated', controller: 'enumeratedType', action: 'setDeprecated')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/validate', controller: 'enumeratedType', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/outgoing/$type', controller: 'enumeratedType', action: 'addOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/incoming/$type', controller: 'enumeratedType', action: 'addIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/mapping/$destination', controller: 'enumeratedType', action: 'addMapping')
    }

    void "test EnumeratedTypeUrlMappings GET Request mappings"() {
        given:
        request.method = 'get'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType', controller: 'enumeratedType', action: 'index')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/search/$search', controller: 'enumeratedType', action: 'search')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id', controller: 'enumeratedType', action: 'show')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/outgoing/search', controller: 'enumeratedType', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/outgoing/$type/search', controller: 'enumeratedType', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/outgoing/$type', controller: 'enumeratedType', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/incoming/search', controller: 'enumeratedType', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/incoming/$type/search', controller: 'enumeratedType', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/incoming/$type', controller: 'enumeratedType', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/incoming', controller: 'enumeratedType', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/outgoing', controller: 'enumeratedType', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/mapping', controller: 'enumeratedType', action: 'mappings')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/typeHierarchy', controller: 'enumeratedType', action: 'typeHierarchy')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/history', controller: 'enumeratedType', action: 'history')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/path', controller: 'enumeratedType', action: 'path')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/dataElement', controller: 'enumeratedType', action: 'dataElements')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/convert/$destination', controller: 'enumeratedType', action: 'convert')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/validateValue', controller: 'enumeratedType', action: 'validateValue')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/content', controller: 'enumeratedType', action: 'content')
    }

    void "test EnumeratedTypeUrlMappings PUT Request mappings"() {
        given:
        request.method = 'put'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id', controller: 'enumeratedType', action: 'update')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/outgoing/$type', controller: 'enumeratedType', action: 'reorderOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/incoming/$type', controller: 'enumeratedType', action: 'reorderIncoming')
    }

    void "test EnumeratedTypeUrlMappings delete Request mappings"() {
        given:
        request.method = 'delete'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id', controller: 'enumeratedType', action: 'delete')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/outgoing/$type', controller: 'enumeratedType', action: 'removeOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/incoming/$type', controller: 'enumeratedType', action: 'removeIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/enumeratedType/$id/mapping/$destination', controller: 'enumeratedType', action: 'removeMapping')
    }
}
