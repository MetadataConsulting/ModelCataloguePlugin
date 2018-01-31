package org.modelcatalogue.core

import grails.test.MockUtils
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.cache.CacheService
import org.modelcatalogue.core.security.Role
import rx.Observable
import spock.lang.Specification
import spock.util.concurrent.BlockingVariable

@TestFor(CatalogueElementService)
@Mock([DataClass, ReferenceType, Role])
class CatalogueElementServiceSpec extends Specification {

    def "test if modelCatalogueSearchService unindex method is called when deleting CatalogueElement"() {
        setup:
            service.manualDeleteRelationshipsService = Mock(ManualDeleteRelationshipsService)
            def searchCatalogue = Mock(SearchCatalogue)
            def auditService = Mock(AuditService)
            def cacheService = Mock(CacheService)
            def securityService = Mock(ModelCatalogueSecurityService)

            auditService.modelCatalogueSearchService = searchCatalogue

            service.cacheService = cacheService

            def dataClass = new DataClass(name: "Alexander Lukashenko")
            dataClass.auditService = auditService // there is no better way how to mock collaborating service
            dataClass.modelCatalogueSecurityService = securityService
            dataClass.save()

            def subscribed = new BlockingVariable<CatalogueElement>()

            service.modelCatalogueSearchService = searchCatalogue


        when:
            service.delete(dataClass)

        then:
            1 * searchCatalogue.unindex(dataClass) >> { CatalogueElement catalogueElement ->
                return Observable.just(true).doOnSubscribe {
                    subscribed.set(catalogueElement)
                }
            }

            0 * searchCatalogue.unindex(_)

        and:
            dataClass == subscribed.get()
    }
}
