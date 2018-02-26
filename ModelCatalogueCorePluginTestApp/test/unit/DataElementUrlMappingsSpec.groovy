import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.DataElementController
import org.springframework.http.HttpMethod
import spock.lang.Specification
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(DataElementUrlMappings)
@Mock(DataElementController)
class DataElementUrlMappingsSpec extends Specification {
    void "test DataElementUrlMappings POST Request mappings"() {
        given:
        request.method = 'post'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement', controller: 'dataElement', action: 'save')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/validate', controller: 'dataElement', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/validate', controller: 'dataElement', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/restore', controller: 'dataElement', action: 'restore')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/clone/$destinationDataModelId', controller: 'dataElement', action: 'cloneElement')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$source/merge/$destination', controller: 'dataElement', action: 'merge')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/archive', controller: 'dataElement', action: 'archive')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/outgoing/$type', controller: 'dataElement', action: 'addOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/incoming/$type', controller: 'dataElement', action: 'addIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/mapping/$destination', controller: 'dataElement', action: 'addMapping')
    }

    void "test DataElementUrlMappings GET Request mappings"() {
        given:
        request.method = 'get'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement', controller: 'dataElement', action: 'index')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/search/$search', controller: 'dataElement', action: 'search')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id', controller: 'dataElement', action: 'show')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/outgoing/search', controller: 'dataElement', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/outgoing/$type/search', controller: 'dataElement', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/outgoing/$type', controller: 'dataElement', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/incoming/search', controller: 'dataElement', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/incoming/$type/search', controller: 'dataElement', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/incoming/$type', controller: 'dataElement', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/incoming', controller: 'dataElement', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/outgoing', controller: 'dataElement', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/mapping', controller: 'dataElement', action: 'mappings')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/typeHierarchy', controller: 'dataElement', action: 'typeHierarchy')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/history', controller: 'dataElement', action: 'history')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/path', controller: 'dataElement', action: 'path')
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/content', controller: 'dataElement', action: 'content')
    }

    void "test DataElementUrlMappings PUT Request mappings"() {
        given:
        request.method = 'put'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id', controller: 'dataElement', action: 'update', method: HttpMethod.PUT)
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/incoming/$type', controller: 'dataElement', action: 'reorderIncoming', method: HttpMethod.PUT)
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/outgoing/$type', controller: 'dataElement', action: 'reorderOutgoing', method: HttpMethod.PUT)

    }

    void "test DataElementUrlMappings delete Request mappings"() {
        given:
        request.method = 'delete'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/incoming/$type', controller: 'dataElement', action: 'removeIncoming', method: HttpMethod.DELETE)
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id', controller: 'dataElement', action: 'delete', method: HttpMethod.DELETE)
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/mapping/$destination', controller: 'dataElement', action: 'removeMapping', method: HttpMethod.DELETE)
        assertForwardUrlMapping('/api/modelCatalogue/core/dataElement/$id/outgoing/$type', controller: 'dataElement', action: 'removeOutgoing', method: HttpMethod.DELETE)

    }
}
