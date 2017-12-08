import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.UserController
import spock.lang.Specification

@TestFor(UserUrlMappings)
@Mock(UserController)
class UserUrlMappingSpec extends Specification {

    void "test UserUrlMappings GET Request mappings"() {
        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/user', controller: 'user', action: 'index')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/search/$search', controller: 'user', action: 'search')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id', controller: 'user', action: 'show')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/outgoing/search', controller: 'user', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/outgoing/$type/search', controller: 'user', action: 'searchOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/outgoing/$type', controller: 'user', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/incoming/search', controller: 'user', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/incoming/$type/search', controller: 'user', action: 'searchIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/incoming/$type', controller: 'user', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/incoming', controller: 'user', action: 'incoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/outgoing', controller: 'user', action: 'outgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/mapping', controller: 'user', action: 'mappings')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/typeHierarchy', controller: 'user', action: 'typeHierarchy')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/history', controller: 'user', action: 'history')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/path', controller: 'user', action: 'path')
        assertForwardUrlMapping('/user/current', controller: 'user', action: 'current')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/current', controller: 'user', action: 'current')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/lastSeen', controller: 'user', action: 'lastSeen')
    }

    void "test UserUrlMappings POST Request mappings"() {
        given:
        request.method = 'post'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/user', controller: 'user', action: 'save')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/validate', controller: 'user', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/validate', controller: 'user', action: 'validate')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/outgoing/$type', controller: 'user', action: 'addOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/incoming/$type', controller: 'user', action: 'addIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/mapping/$destination', controller: 'user', action: 'addMapping')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/archive', controller: 'user', action: 'archive')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/restore', controller: 'user', action: 'restore')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$source/merge/$destination', controller: 'user', action: 'merge')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/classifications', controller: 'user', action: 'classifications')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/apikey', controller: 'user', action: 'apiKey')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/favourite', controller: 'user', action: 'addFavourite')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/enable', controller: 'user', action: 'enable')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/disable', controller: 'user', action: 'disable')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/role/$role', controller: 'user', action: 'role')
    }

    void "test UserUrlMapping PUT request mapping"() {
        given:
        request.method = 'put'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id', controller: 'user', action: 'update')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/outgoing/$type', controller: 'user', action: 'reorderOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/incoming/$type', controller: 'user', action: 'reorderIncoming')
    }

    void "test UserUrlMapping DELETE request mapping"() {
        given:
        request.method = 'delete'

        expect:
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id', controller: 'user', action: 'delete')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/outgoing/$type', controller: 'user', action: 'removeOutgoing')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/incoming/$type', controller: 'user', action: 'removeIncoming')
        assertForwardUrlMapping('/api/modelCatalogue/core/user/$id/favourite', controller: 'user', action: 'removeFavourite')
    }
}
