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
        publishedElementService.list(status: ElementStatus.DRAFT).size()               == 17
        publishedElementService.list(status: ElementStatus.DRAFT, max: 10).size()      == 10
        publishedElementService.list(Model, status: 'DRAFT').size()                             == 7
        publishedElementService.list(Model, status: ElementStatus.DRAFT).size()        == 7
        publishedElementService.list(DataElement, status: 'DRAFT').size()                       == 5
        publishedElementService.list(DataElement, status: ElementStatus.DRAFT).size()  == 5
        publishedElementService.list(Asset, status: 'DRAFT').size()                             == 5
        publishedElementService.list(Asset, status: ElementStatus.DRAFT).size()        == 5
        publishedElementService.count(status: 'DRAFT')                                          == 17
        publishedElementService.count(status: ElementStatus.DRAFT)                     == 17
        publishedElementService.count(Model, status: 'DRAFT')                                   == 7
        publishedElementService.count(Model, status: ElementStatus.DRAFT)              == 7
        publishedElementService.count(DataElement, status: 'DRAFT')                             == 5
        publishedElementService.count(DataElement, status: ElementStatus.DRAFT)        == 5
        publishedElementService.count(Asset, status: 'DRAFT')                             == 5
        publishedElementService.count(Asset, status: ElementStatus.DRAFT)        == 5
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
        author.status == ElementStatus.DRAFT
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

    def "merge"() {
        Classification sact = new Classification(name: "SACT").save(failOnError: true)
        Classification cosd = new Classification(name: "COSD").save(failOnError: true)

        ValueDomain domain = new ValueDomain(name: "merger test domain").save(failOnError: true)
        DataElement source = new DataElement(name: "merge tester", valueDomain: domain, classifications: [sact]).save(failOnError: true)
        DataElement destination = new DataElement(name: "merge tester", classifications: [cosd]).save(failOnError: true)
        Model m1 = new Model(name: 'merge test container 1').save(failOnError: true)
        Model m2 = new Model(name: 'merge test container 2').save(failOnError: true)

        Model m3cosd = new Model(name: 'merge test container 3', classifications: [cosd]).save(failOnError: true)
        Model m3sact = new Model(name: 'merge test container 3', classifications: [sact]).save(failOnError: true)

        m1.addToContains(source)
        m2.addToContains(destination)

        m3cosd.addToContains(destination)
        m3sact.addToContains(source)

        source.ext.one = 'one'
        source.ext.two = '2'

        destination.ext.two = 'two'
        destination.ext.three = 'three'

        def merged = publishedElementService.merge(source, destination)

        expect:
        merged.errors.errorCount == 0
        merged == destination
        merged.valueDomain == domain
        destination.ext.size() == 3
        destination.ext.two == 'two'
        source.countContainedIn() == 2
        destination.countContainedIn() == 3
        source.classifications.size() == 0
        destination.classifications.size() == 2
        source.archived
        destination.supersededBy.contains source
        !m3cosd.archived
        m3sact.archived

        cleanup:
        source.ext.clear()
        destination.ext.clear()
        source.beforeDelete()
        source.delete()
        destination.beforeDelete()
        destination.delete()
        domain.delete()
        m1.delete()
        m2.delete()
        m3cosd.delete()
        m3sact.delete()
        sact.delete()
        cosd.delete()
    }

    def "create new version of hierarchy model"() {

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
        md1.status == ElementStatus.DRAFT
        md2.status == ElementStatus.DRAFT
        md3.status == ElementStatus.DRAFT
        md4.status == ElementStatus.DRAFT
        de1.status == ElementStatus.DRAFT
        de2.status == ElementStatus.DRAFT
        de3.status == ElementStatus.DRAFT

        when:

        publishedElementService.finalizeTree(md1)

        then:
        md1.status == ElementStatus.FINALIZED
        md2.status == ElementStatus.FINALIZED
        md3.status == ElementStatus.FINALIZED
        md4.status == ElementStatus.FINALIZED
        de1.status == ElementStatus.FINALIZED
        de2.status == ElementStatus.FINALIZED
        de3.status == ElementStatus.FINALIZED

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
        md1.status == ElementStatus.DRAFT
        md2.status == ElementStatus.DRAFT
        md3.status == ElementStatus.DRAFT

        when:
        publishedElementService.finalizeTree(md1)

        then:
        md1.status == ElementStatus.FINALIZED
        md2.status == ElementStatus.FINALIZED
        md3.status == ElementStatus.FINALIZED

        cleanup:
        md1.delete()
        md2.delete()
        md3.delete()


    }

}
