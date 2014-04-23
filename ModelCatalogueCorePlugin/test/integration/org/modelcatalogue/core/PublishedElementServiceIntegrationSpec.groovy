package org.modelcatalogue.core

class PublishedElementServiceIntegrationSpec extends AbstractIntegrationSpec {

    def setupSpec() {
        loadFixtures()
    }

    def publishedElementService

    def "return only finalized elements by default"() {
        expect:
        publishedElementService.list().size()               == 12
        publishedElementService.list(max: 10).size()        == 10
        publishedElementService.list(DataElement).size()    == 7
        publishedElementService.list(Model).size()          == 5
        publishedElementService.count()                     == 12
        publishedElementService.count(DataElement)          == 7
        publishedElementService.count(Model)                == 5
    }

    def "can supply status as parameter"() {
        expect:
        publishedElementService.list(status: 'DRAFT').size()                                    == 12
        publishedElementService.list(status: 'DRAFT', max: 10).size()                           == 10
        publishedElementService.list(status: PublishedElementStatus.DRAFT).size()               == 12
        publishedElementService.list(status: PublishedElementStatus.DRAFT, max: 10).size()      == 10
        publishedElementService.list(Model, status: 'DRAFT').size()                             == 7
        publishedElementService.list(Model, status: PublishedElementStatus.DRAFT).size()        == 7
        publishedElementService.list(DataElement, status: 'DRAFT').size()                       == 5
        publishedElementService.list(DataElement, status: PublishedElementStatus.DRAFT).size()  == 5
        publishedElementService.count(status: 'DRAFT')                                          == 12
        publishedElementService.count(status: PublishedElementStatus.DRAFT)                     == 12
        publishedElementService.count(Model, status: 'DRAFT')                                   == 7
        publishedElementService.count(Model, status: PublishedElementStatus.DRAFT)              == 7
        publishedElementService.count(DataElement, status: 'DRAFT')                             == 5
        publishedElementService.count(DataElement, status: PublishedElementStatus.DRAFT)        == 5
    }

}
