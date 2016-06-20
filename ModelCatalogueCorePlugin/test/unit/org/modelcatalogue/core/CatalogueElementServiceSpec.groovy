package org.modelcatalogue.core

import grails.test.MockUtils
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.audit.AuditService
import rx.Observable
import spock.lang.Specification
import spock.util.concurrent.BlockingVariable

@TestFor(CatalogueElementService)
@Mock(DataClass)
class CatalogueElementServiceSpec extends Specification {

    def "test if modelCatalogueSearchService unindex method is called when deleting CatalogueElement"() {
        setup:
            def searchCatalogue = Mock(SearchCatalogue)
            def auditService = Mock(AuditService)
            auditService.modelCatalogueSearchService = searchCatalogue

            def dataClass = new DataClass(name: "Alexander Lukashenko")
            dataClass.auditService = auditService // there is no better way how to mock collaborating service
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
