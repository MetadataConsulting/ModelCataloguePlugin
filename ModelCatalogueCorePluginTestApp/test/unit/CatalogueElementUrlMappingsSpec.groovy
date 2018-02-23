import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.CatalogueElementController
import spock.lang.Specification
import spock.lang.IgnoreIf

@IgnoreIf({ System.getProperty('IGNORE_URLMAPPINGS') })
@TestFor(CatalogueElementUrlMappings)
@Mock(CatalogueElementController)
class CatalogueElementUrlMappingsSpec extends Specification {
    void "test CatalogueElementUrlMappings POST Request mappings"() {
        given:
        request.method = 'post'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement', controller: 'catalogueElement', action: 'save')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/validate', controller: 'catalogueElement', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/validate', controller: 'catalogueElement', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/archive', controller: 'catalogueElement', action: 'archive')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/restore', controller: 'catalogueElement', action: 'restore')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/clone/$destinationDataModelId', controller: 'catalogueElement', action: 'cloneElement')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$source/merge/$destination', controller: 'catalogueElement', action: 'merge')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/outgoing/$type', controller: 'catalogueElement', action: 'addOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/incoming/$type', controller: 'catalogueElement', action: 'addIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/mapping/$destination', controller: 'catalogueElement', action: 'addMapping')
    }

    void "test CatalogueElementUrlMappings GET Request mappings"() {
        given:
        request.method = 'get'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement', controller: 'catalogueElement', action: 'index')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/search/$search', controller: 'catalogueElement', action: 'search')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id', controller: 'catalogueElement', action: 'show')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/outgoing/search', controller: 'catalogueElement', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/outgoing/$type/search', controller: 'catalogueElement', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/outgoing/$type', controller: 'catalogueElement', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/incoming/search', controller: 'catalogueElement', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/incoming/$type/search', controller: 'catalogueElement', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/incoming/$type', controller: 'catalogueElement', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/incoming', controller: 'catalogueElement', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/outgoing', controller: 'catalogueElement', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/mapping', controller: 'catalogueElement', action: 'mappings')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/typeHierarchy', controller: 'catalogueElement', action: 'typeHierarchy')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/history', controller: 'catalogueElement', action: 'history')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/path', controller: 'catalogueElement', action: 'path')
    }

    void "test CatalogueElementUrlMappings PUT Request mappings"() {
        given:
        request.method = 'put'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id', controller: 'catalogueElement', action: 'update')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/outgoing/$type', controller: 'catalogueElement', action: 'reorderOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/incoming/$type', controller: 'catalogueElement', action: 'reorderIncoming')
    }

    void "test CatalogueElementUrlMappings delete Request mappings"() {
        given:
        request.method = 'delete'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id', controller: 'catalogueElement', action: 'delete')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/outgoing/$type', controller: 'catalogueElement', action: 'removeOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/mapping/$destination', controller: 'catalogueElement', action: 'removeMapping')
        assertForwardUrlMapping('/api/modelCatalogue/core/catalogueElement/$id/incoming/$type', controller: 'catalogueElement', action: 'removeIncoming')
    }
}
