package org.modelcatalogue.core

import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.util.RelationshipDirection
import spock.lang.Issue
import spock.lang.Unroll

class ElementServiceIntegrationSpec extends AbstractIntegrationSpec {

    def setup() {
        loadFixtures()
    }

    def elementService
    def relationshipService
    def mappingService

    def "return finalized and draft elements by default"() {
        expect:
        elementService.list().size()                == CatalogueElement.countByStatusInList([ElementStatus.FINALIZED, ElementStatus.DRAFT])
        elementService.list(max: 10).size()         == 10
        elementService.list(DataElement).size()     == DataElement.countByStatusInList([ElementStatus.FINALIZED, ElementStatus.DRAFT])
        elementService.list(DataClass).size()       == DataClass.countByStatusInList([ElementStatus.FINALIZED, ElementStatus.DRAFT])
        elementService.list(Asset).size()           == Asset.countByStatusInList([ElementStatus.FINALIZED, ElementStatus.DRAFT])
        elementService.count()                      == CatalogueElement.countByStatusInList([ElementStatus.FINALIZED, ElementStatus.DRAFT])
        elementService.count(DataElement)           == DataElement.countByStatusInList([ElementStatus.FINALIZED, ElementStatus.DRAFT])
        elementService.count(DataClass)             == DataClass.countByStatusInList([ElementStatus.FINALIZED, ElementStatus.DRAFT])
        elementService.count(Asset)                 == Asset.countByStatusInList([ElementStatus.FINALIZED, ElementStatus.DRAFT])
    }

    def "can supply status as parameter"() {
        expect:
        elementService.list(status: 'DRAFT').size()                             == 17
        elementService.list(status: 'DRAFT', max: 10).size()                    == 10
        elementService.list(status: ElementStatus.DRAFT).size()                 == 17
        elementService.list(status: ElementStatus.DRAFT, max: 10).size()        == 10
        elementService.list(DataClass, status: 'DRAFT').size()                  == 7
        elementService.list(DataClass, status: ElementStatus.DRAFT).size()      == 7
        elementService.list(DataElement, status: 'DRAFT').size()                == 5
        elementService.list(DataElement, status: ElementStatus.DRAFT).size()    == 5
        elementService.list(Asset, status: 'DRAFT').size()                      == 5
        elementService.list(Asset, status: ElementStatus.DRAFT).size()          == 5
        elementService.count(status: 'DRAFT')                                   == 17
        elementService.count(status: ElementStatus.DRAFT)                       == 17
        elementService.count(DataClass, status: 'DRAFT')                        == 7
        elementService.count(DataClass, status: ElementStatus.DRAFT)            == 7
        elementService.count(DataElement, status: 'DRAFT')                      == 5
        elementService.count(DataElement, status: ElementStatus.DRAFT)          == 5
        elementService.count(Asset, status: 'DRAFT')                            == 5
        elementService.count(Asset, status: ElementStatus.DRAFT)                == 5
    }


    def "create new version"() {
        DataElement author = DataElement.findByName('auth5')
        DataType domain = DataType.findByName('test1')


        author.ext.something = 'anything'
        author.dataType = domain

        int originalVersion     = author.versionNumber
        DataElement draft       = elementService.createDraftVersion(author, DraftContext.forceNew()) as DataElement
        int draftVersion        = draft.versionNumber
        int newVersion          = author.versionNumber
        author.refresh()

        expect:
        author != draft
        author.id != draft.id
        author.versionCreated != author.dateCreated
        originalVersion == newVersion
        draftVersion    == originalVersion + 1

        draft.ext.something == 'anything'

        draft.supersedes.contains(author)

        author.dataType
        draft.dataType

        author.status == ElementStatus.FINALIZED
        draft.status == ElementStatus.DRAFT

        when:
        def anotherDraft = elementService.createDraftVersion(draft, DraftContext.forceNew())

        println "Author Supersedes: $author.supersedes"
        println "Draft Supersedes: $draft.supersedes"
        println "Another Draft Supersedes:  $anotherDraft.supersedes"

        then:
        draft.countSupersedes()             == 1
        author.countSupersedes()            == 0
        anotherDraft.countSupersedes()      == 1
        draft.countSupersededBy()           == 1
        author.countSupersededBy()          == 1
        anotherDraft.countSupersededBy()    == 0

        anotherDraft.supersedes.contains(draft)
        anotherDraft.status == ElementStatus.DRAFT
        draft.status        == ElementStatus.DEPRECATED
        author.status       == ElementStatus.FINALIZED

        author.latestVersionId          == author.id
        draft.latestVersionId           == author.id
        anotherDraft.latestVersionId    == author.id

    }

    def "archive"() {
        DataElement author = DataElement.findByName('auth5')
        DataType dataType = DataType.findByName('test1')

        expect:
        author
        dataType


        when:
        author.ext.something = 'anything'
        author.dataType = dataType
        author.save(failOnError: true)

        int originalVersion     = author.versionNumber
        DataElement archived    = elementService.archive(author, true) as DataElement
        int archivedVersion     = archived.versionNumber
        author.refresh()

        then:
        author == archived
        author.id == archived.id
        originalVersion == archivedVersion
        archived.incomingRelationships.every { it.archived }
        archived.outgoingRelationships.every { it.archived }

        archived.ext.something == 'anything'

        !(archived in dataType.relatedDataElements)
    }

    def "merge"() {
        DataModel sact = new DataModel(name: "SACT").save(failOnError: true)
        DataModel cosd = new DataModel(name: "COSD").save(failOnError: true)

        DataType dataType = new DataType(name: "merger test type").save(failOnError: true)

        DataElement source = new DataElement(dataModel: sact,  name: "merge tester", dataType: dataType).save(failOnError: true)

        DataElement destination = new DataElement(dataModel: cosd, name: "merge tester").save(failOnError: true)

        DataClass m1 = new DataClass(name: 'merge test container 1').save(failOnError: true)
        DataClass m2 = new DataClass(name: 'merge test container 2').save(failOnError: true)

        DataClass m3cosd = new DataClass(dataModel: cosd, name: 'merge test container 3').save(failOnError: true)

        DataClass m3sact = new DataClass(dataModel: sact, name: 'merge test container 3').save(failOnError: true)

        m1.addToContains(source)
        m2.addToContains(destination)

        m3cosd.addToContains(destination)
        m3sact.addToContains(source)

        source.ext.one = 'one'
        source.ext.two = '2'

        destination.ext.two = 'two'
        destination.ext.three = 'three'

        def merged = elementService.merge(source, destination)

        expect:
        merged.errors.errorCount == 0
        merged == destination
        merged.dataType == dataType
        destination.ext.size() == 3
        destination.ext.two == 'two'
        source.countContainedIn() == 2
        destination.countContainedIn() == 3
        source.dataModels.size() == 1
        destination.dataModels.size() == 1
        source.archived
        destination.supersededBy.contains source
        !m3cosd.archived
        m3sact.archived

        cleanup:
        source?.ext?.clear()
        destination?.ext?.clear()
        source?.beforeDelete()
        source?.delete()
        destination?.beforeDelete()
        destination?.delete()
        dataType?.delete()
        m1?.delete()
        m2?.delete()
        m3cosd?.delete()
        m3sact?.delete()
        sact?.delete()
        cosd?.delete()
    }

    def "create new version of hierarchy model"() {

        setup:
        DataClass md1      = new DataClass(name:"test1").save()
        DataClass md2      = new DataClass(name:"test2").save()
        DataClass md3      = new DataClass(name:"test3").save()

        md1.addToParentOf(md2)
        md2.addToParentOf(md3)

        elementService.finalizeElement(md1)
        elementService.finalizeElement(md2)
        elementService.finalizeElement(md3)


        int originalVersion     = md2.versionNumber
        DataClass draft             = elementService.createDraftVersion(md2, DraftContext.userFriendly()) as DataClass
        int draftVersion        = draft.versionNumber
        int newVersion          = md2.versionNumber

        List<Relationship> draftRelationships = relationshipService.getRelationships([:], RelationshipDirection.BOTH, draft, RelationshipType.hierarchyType).items

        expect:
        md2 != draft
        md2.id != draft.id
        originalVersion == newVersion
        draftVersion == originalVersion + 1

        draft.supersedes.contains(md2)

        md2.parentOf.contains(md3)
        md2.childOf.contains(md1)
        md2.parentOf.contains(md3)
        !md1.parentOf.contains(draft)
        draft.parentOf.contains(md3)
        draftRelationships.size() == 2

        cleanup:
        md1.delete()
        md2.delete()
        md3.delete()


    }

    def "finalize element"(){
        when:
        DataElement author = DataElement.findByName('auth5')
        DataElement draft = elementService.createDraftVersion(author, DraftContext.userFriendly()) as DataElement

        then:
        draft.status    == ElementStatus.DRAFT
        author.status   == ElementStatus.FINALIZED

        elementService.finalizeElement(draft)

        then:
        draft.status    == ElementStatus.FINALIZED
        author.status   == ElementStatus.DEPRECATED
    }

    def "finalize tree"(){

        setup:
        DataClass md1      = new DataClass(name:"test1").save()
        DataClass md2      = new DataClass(name:"test2").save()
        DataClass md3      = new DataClass(name:"test3").save()
        DataClass md4      = new DataClass(name:"test3").save()
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

        elementService.finalizeElement(md1)

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
        DataClass md1      = new DataClass(name:"test1").save()
        DataClass md2      = new DataClass(name:"test2").save()
        DataClass md3      = new DataClass(name:"test3").save()

        md1.addToParentOf(md2)
        md2.addToParentOf(md3)
        md3.addToParentOf(md1)

        expect:
        md1.status == ElementStatus.DRAFT
        md2.status == ElementStatus.DRAFT
        md3.status == ElementStatus.DRAFT

        when:
        md1 = elementService.finalizeElement(md1)

        then:
        md1.errors.errorCount == 0
        md2.errors.errorCount == 0
        md3.errors.errorCount == 0

        md1.status == ElementStatus.FINALIZED
        md2.status == ElementStatus.FINALIZED
        md3.status == ElementStatus.FINALIZED

        cleanup:
        md1.delete()
        md2.delete()
        md3.delete()


    }

    def "change value domains in data elements while merging value domains"() {
        DataType vd1 = new DataType(name: "vd1").save(failOnError: true)
        DataType vd2 = new DataType(name: "vd2").save(failOnError: true)

        DataElement de = new DataElement(name: "de", dataType: vd1).save(failOnError: true)

        expect:
        de.dataType == vd1

        when:
        elementService.merge(vd1, vd2)

        then:
        de.dataType == vd2
    }


    def "mappings are transferred to the new draft"() {
        DataType d1 = new DataType(name: "VD4MT1", status: ElementStatus.FINALIZED).save(failOnError: true, flush: true)
        DataType d2 = new DataType(name: "VD4MT2", status: ElementStatus.FINALIZED).save(failOnError: true, flush: true)

        Mapping mapping = mappingService.map(d1, d2, "x")

        expect:
        mapping.errors.errorCount == 0

        when:
        DataType d1draft = elementService.createDraftVersion(d1, DraftContext.userFriendly())

        then:
        d1draft
        d1draft.outgoingMappings
        d1draft.outgoingMappings.size() == 1
        d1draft.outgoingMappings[0].destination == d2

    }

    @Unroll
    def "can change data type type when creating new draft to #type"() {
        DataType d1 = new DataType(name: "DT4CDT").save(failOnError: true, flush: true)
        DataElement element = new DataElement(name: 'DE4MET-732', dataType: d1).save(failOnError: true, flush: true)

        expect:
        element in d1.relatedDataElements

        when:
        DataType d1draft = elementService.createDraftVersion(d1, DraftContext.typeChangingUserFriendly(type))

        then:
        d1draft
        d1draft.errors.errorCount == 0
        d1draft.instanceOf(type)
        d1draft.name == "DT4CDT"
        element in d1draft.relatedDataElements

        where:
        type << [PrimitiveType, EnumeratedType, ReferenceType]
    }

    @Issue("https://metadata.atlassian.net/browse/MET-732")
    def "can un-deprecate element if conditions are met"() {
        DataType vd = new DataType(name: 'VD4MET-732').save(failOnError: true, flush: true)

        vd = elementService.createDraftVersion(vd, DraftContext.importFriendly([] as Set)) as DataType
        vd = elementService.finalizeElement(vd)
        vd = elementService.archive(vd, false)

        when:
        elementService.restore(vd)

        then:
        vd.status == ElementStatus.FINALIZED
    }

}
