package org.modelcatalogue.core

import org.modelcatalogue.core.util.RelationshipDirection

class PublishedElementServiceIntegrationSpec extends AbstractIntegrationSpec {

    def setupSpec() {
        loadFixtures()
    }

    def publishedElementService
    def relationshipService

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


    def "create new version"() {
        DataElement author      = DataElement.findByName('DE_author')
        ValueDomain domain      = ValueDomain.findByName('value domain test1')

        author.addToInstantiatedBy(domain)

        int originalVersion     = author.versionNumber
        DataElement archived    = publishedElementService.archiveAndIncreaseVersion(author)
        int archivedVersion     = archived.versionNumber
        int newVersion          = author.versionNumber

        expect:
        author != archived
        author.id != archived.id
        originalVersion != newVersion
        originalVersion == newVersion - 1
        archivedVersion == originalVersion

        author.supersedes.contains(archived)

        author.instantiatedBy.size()    == 1
        archived.instantiatedBy.size()  == 1
        domain.instantiates.size()      == 1

        relationshipService.getRelationships([:], RelationshipDirection.BOTH, author, RelationshipType.instantiationType).list.size() == 1
        relationshipService.getRelationships([:], RelationshipDirection.BOTH, archived, RelationshipType.instantiationType).list.size() == 1
        relationshipService.getRelationships([:], RelationshipDirection.BOTH, domain, RelationshipType.instantiationType).list.size() == 1

        when:
        def anotherArchived = publishedElementService.archiveAndIncreaseVersion(author)

        then:
        archived.countSupersedes()        == 0
        anotherArchived.countSupersedes() == 1
        author.countSupersedes()          == 1
        author.supersedes.contains(anotherArchived)
        anotherArchived.supersedes.contains(archived)

    }

    def "create new version of a data element, as it is contained in a model this updates the containing model (i.e. the model is not pending)"() {
        DataElement author = DataElement.findByName('DE_author')
        Model book = Model.findByNameAndStatus("book", "FINALIZED")
        book.addToContains(author)

        when:
        def anotherArchived = publishedElementService.archiveAndIncreaseVersion(author)
        Model oldBook = Model.findByNameAndStatus("book", "ARCHIVED")

        then:

        author.supersedes.contains(anotherArchived)
        book.supersedes.contains(oldBook)
        book.contains.contains(author)
        !book.contains.contains(anotherArchived)
        oldBook.contains.contains(anotherArchived)
    }

    def "create new version of data element that doesn't updated containing model whilst the model is pending"() {
        DataElement author = DataElement.findByName('auth')
        Model book = Model.findByName("chapter1")
        book.addToContains(author)

        when:
        book.status = PublishedElementStatus.PENDING
        book.save(flush:true)
        def anotherArchived = publishedElementService.archiveAndIncreaseVersion(author)
        Model oldBook = Model.findByNameAndStatus("chapter1", "ARCHIVED")

        then:

        !oldBook
        author.supersedes.contains(anotherArchived)
        book.contains.contains(author)
        author.status==PublishedElementStatus.PENDING

    }

}
