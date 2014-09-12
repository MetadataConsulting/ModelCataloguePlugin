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
        DataElement author      = DataElement.findByName('auth5')
        ValueDomain domain      = ValueDomain.findByName('value domain test1')


        author.ext.something = 'anything'
        author.valueDomain = domain

        int originalVersion     = author.versionNumber
        DataElement archived    = publishedElementService.archiveAndIncreaseVersion(author)
        int archivedVersion     = archived.versionNumber
        int newVersion          = author.versionNumber
        author.refresh()

        expect:
        author != archived
        author.id != archived.id
        author.versionCreated != author.dateCreated
        originalVersion != newVersion
        originalVersion == newVersion - 1
        archivedVersion == originalVersion

        archived.ext.something == 'anything'

        author.supersedes.contains(archived)

        author.valueDomain
        archived.valueDomain
        domain.dataElements.size() == 1

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

    def "archive"() {
        DataElement author      = DataElement.findByName('auth5')
        ValueDomain domain      = ValueDomain.findByName('value domain test1')


        author.ext.something = 'anything'
        author.valueDomain = domain
        author.save(failOnError: true)

        int originalVersion     = author.versionNumber
        DataElement archived    = publishedElementService.archive(author)
        int archivedVersion     = archived.versionNumber
        author.refresh()

        expect:
        author == archived
        author.id == archived.id
        originalVersion == archivedVersion
        archived.incomingRelationships.every { it.archived }
        archived.outgoingRelationships.every { it.archived }

        archived.ext.something == 'anything'

        !archived.valueDomain
        !(archived in domain.dataElements)
    }

    def "create new version of heirachy model"() {

        setup:
        Model md1      = new Model(name:"test1").save()
        Model md2      = new Model(name:"test2").save()
        Model md3      = new Model(name:"test3").save()

        md1.addToParentOf(md2)
        md2.addToParentOf(md3)

        int originalVersion     = md2.versionNumber
        Model archived    = publishedElementService.archiveAndIncreaseVersion(md2)
        int archivedVersion     = archived.versionNumber
        int newVersion          = md2.versionNumber

        def archivedrel = relationshipService.getRelationships([:], RelationshipDirection.BOTH, archived, RelationshipType.hierarchyType).items

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

        cleanup:
        md1.delete()
        md2.delete()
        md3.delete()


    }

    def "finalize tree"(){

        setup:
        Model md1      = new Model(name:"test1").save()
        Model md2      = new Model(name:"test2").save()
        Model md3      = new Model(name:"test3").save()
        Model md4      = new Model(name:"test3").save()
        DataElement de1 = new DataElement(name: "test1").save()
        DataElement de2 = new DataElement(name: "test1").save()
        DataElement de3 = new DataElement(name: "test1").save()

        md1.addToContains(de1)
        md3.addToContains(de2)
        md4.addToContains(de3)
        md1.addToParentOf(md2)
        md1.addToParentOf(md3)
        md2.addToParentOf(md4)

        expect:
        md1.status == PublishedElementStatus.DRAFT
        md2.status == PublishedElementStatus.DRAFT
        md3.status == PublishedElementStatus.DRAFT
        md4.status == PublishedElementStatus.DRAFT
        de1.status == PublishedElementStatus.DRAFT
        de2.status == PublishedElementStatus.DRAFT
        de3.status == PublishedElementStatus.DRAFT

        when:

        publishedElementService.finalizeTree(md1)

        then:
        md1.status == PublishedElementStatus.FINALIZED
        md2.status == PublishedElementStatus.FINALIZED
        md3.status == PublishedElementStatus.FINALIZED
        md4.status == PublishedElementStatus.FINALIZED
        de1.status == PublishedElementStatus.FINALIZED
        de2.status == PublishedElementStatus.FINALIZED
        de3.status == PublishedElementStatus.FINALIZED

        cleanup:
        de1.delete()
        de2.delete()
        de3.delete()
        md4.delete()
        md3.delete()
        md2.delete()
        md1.delete()

    }

    def "finalize tree infinite loop"(){

        setup:
        Model md1      = new Model(name:"test1").save()
        Model md2      = new Model(name:"test2").save()
        Model md3      = new Model(name:"test3").save()

        md1.addToParentOf(md2)
        md2.addToParentOf(md3)
        md3.addToParentOf(md1)

        expect:
        md1.status == PublishedElementStatus.DRAFT
        md2.status == PublishedElementStatus.DRAFT
        md3.status == PublishedElementStatus.DRAFT

        when:
        publishedElementService.finalizeTree(md1)

        then:
        md1.status == PublishedElementStatus.FINALIZED
        md2.status == PublishedElementStatus.FINALIZED
        md3.status == PublishedElementStatus.FINALIZED

        cleanup:
        md1.delete()
        md2.delete()
        md3.delete()


    }

}
