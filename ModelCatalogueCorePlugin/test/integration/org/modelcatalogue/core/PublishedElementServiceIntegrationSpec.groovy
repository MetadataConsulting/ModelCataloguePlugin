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
        publishedElementService.list().size()               == 19
        publishedElementService.list(max: 10).size()        == 10
        publishedElementService.list(DataElement).size()    == 7
        publishedElementService.list(Model).size()          == 5
        publishedElementService.list(Asset).size()          == 7
        publishedElementService.count()                     == 19
        publishedElementService.count(DataElement)          == 7
        publishedElementService.count(Model)                == 5
        publishedElementService.count(Asset)                == 7
    }

    def "can supply status as parameter"() {
        expect:
        publishedElementService.list(status: 'DRAFT').size()                                    == 17
        publishedElementService.list(status: 'DRAFT', max: 10).size()                           == 10
        publishedElementService.list(status: PublishedElementStatus.DRAFT).size()               == 17
        publishedElementService.list(status: PublishedElementStatus.DRAFT, max: 10).size()      == 10
        publishedElementService.list(Model, status: 'DRAFT').size()                             == 7
        publishedElementService.list(Model, status: PublishedElementStatus.DRAFT).size()        == 7
        publishedElementService.list(DataElement, status: 'DRAFT').size()                       == 5
        publishedElementService.list(DataElement, status: PublishedElementStatus.DRAFT).size()  == 5
        publishedElementService.list(Asset, status: 'DRAFT').size()                             == 5
        publishedElementService.list(Asset, status: PublishedElementStatus.DRAFT).size()        == 5
        publishedElementService.count(status: 'DRAFT')                                          == 17
        publishedElementService.count(status: PublishedElementStatus.DRAFT)                     == 17
        publishedElementService.count(Model, status: 'DRAFT')                                   == 7
        publishedElementService.count(Model, status: PublishedElementStatus.DRAFT)              == 7
        publishedElementService.count(DataElement, status: 'DRAFT')                             == 5
        publishedElementService.count(DataElement, status: PublishedElementStatus.DRAFT)        == 5
        publishedElementService.count(Asset, status: 'DRAFT')                             == 5
        publishedElementService.count(Asset, status: PublishedElementStatus.DRAFT)        == 5
    }


    def "create new version"() {
        DataElement author      = DataElement.findByName('DE_author')
        ValueDomain domain      = ValueDomain.findByName('value domain test1')


        author.ext.something = 'anything'
        author.addToInstantiatedBy(domain)

        int originalVersion     = author.versionNumber
        DataElement archived    = publishedElementService.archiveAndIncreaseVersion(author)
        int archivedVersion     = archived.versionNumber
        int newVersion          = author.versionNumber

        expect:
        author != archived
        author.id != archived.id
        author.versionCreated != author.dateCreated
        originalVersion != newVersion
        originalVersion == newVersion - 1
        archivedVersion == originalVersion

        archived.ext.something == 'anything'

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
        author.status == PublishedElementStatus.DRAFT
    }

    def "create new version of heirachy model"() {
        Model md1      = Model.findByName("mTest1")
        Model md2      = Model.findByName("mTest2")
        Model md3      = Model.findByName("mTest3")

        md1.addToParentOf(md2)
        md2.addToParentOf(md3)

        int originalVersion     = md2.versionNumber
        Model archived    = publishedElementService.archiveAndIncreaseVersion(md2)
        int archivedVersion     = archived.versionNumber
        int newVersion          = md2.versionNumber

        def archivedrel = relationshipService.getRelationships([:], RelationshipDirection.BOTH, archived, RelationshipType.hierarchyType).list

        expect:
        md2 != archived
        md2.id != archived.id
        originalVersion != newVersion
        originalVersion == newVersion - 1
        archivedVersion == originalVersion

        md2.supersedes.contains(archived)

        md2.parentOf.contains(md3)
        !md2.childOf.contains(md1)
        md2.parentOf.contains(md3)
        md1.parentOf.contains(archived)
        archived.parentOf.contains(md3)
        archivedrel.size() == 2
        !archivedrel.get(0).archived
        !archivedrel.get(1).archived

    }

}
