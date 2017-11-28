package org.modelcatalogue.core

import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.publishing.CloningContext
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import spock.lang.Issue
import spock.lang.Unroll

class ElementServiceIntegrationSpec extends AbstractIntegrationSpec {

    def setup() {
        loadFixtures()
    }

    def elementService
    def relationshipService
    def mappingService
    CatalogueBuilder catalogueBuilder

//    def "return finalized and draft elements by default"() {
//        expect:
//        elementService.list().size()                == CatalogueElement.countByStatusInList([ElementStatus.FINALIZED, ElementStatus.DRAFT])
//        elementService.list(max: 10).size()         == 10
//        elementService.list(DataElement).size()     == DataElement.countByStatusInList([ElementStatus.FINALIZED, ElementStatus.DRAFT])
//        elementService.list(DataClass).size()       == DataClass.countByStatusInList([ElementStatus.FINALIZED, ElementStatus.DRAFT])
//        elementService.list(Asset).size()           == Asset.countByStatusInList([ElementStatus.FINALIZED, ElementStatus.DRAFT])
//        elementService.count()                      == CatalogueElement.countByStatusInList([ElementStatus.FINALIZED, ElementStatus.DRAFT])
//        elementService.count(DataElement)           == DataElement.countByStatusInList([ElementStatus.FINALIZED, ElementStatus.DRAFT])
//        elementService.count(DataClass)             == DataClass.countByStatusInList([ElementStatus.FINALIZED, ElementStatus.DRAFT])
//        elementService.count(Asset)                 == Asset.countByStatusInList([ElementStatus.FINALIZED, ElementStatus.DRAFT])
//    }
//
//    def "can supply status as parameter"() {
//        expect:
//        elementService.list(status: 'DRAFT').size()                             == CatalogueElement.countByStatus(ElementStatus.DRAFT)
//        elementService.list(status: 'DRAFT', max: 10).size()                    == 10
//        elementService.list(status: ElementStatus.DRAFT).size()                 == CatalogueElement.countByStatus(ElementStatus.DRAFT)
//        elementService.list(status: ElementStatus.DRAFT, max: 10).size()        == 10
//        elementService.list(DataClass, status: 'DRAFT').size()                  == 7
//        elementService.list(DataClass, status: ElementStatus.DRAFT).size()      == 7
//        elementService.list(DataElement, status: 'DRAFT').size()                == 5
//        elementService.list(DataElement, status: ElementStatus.DRAFT).size()    == 5
//        elementService.list(Asset, status: 'DRAFT').size()                      == 5
//        elementService.list(Asset, status: ElementStatus.DRAFT).size()          == 5
//        elementService.count(status: 'DRAFT')                                   == CatalogueElement.countByStatus(ElementStatus.DRAFT)
//        elementService.count(status: ElementStatus.DRAFT)                       == CatalogueElement.countByStatus(ElementStatus.DRAFT)
//        elementService.count(DataClass, status: 'DRAFT')                        == 7L
//        elementService.count(DataClass, status: ElementStatus.DRAFT)            == 7L
//        elementService.count(DataElement, status: 'DRAFT')                      == 5L
//        elementService.count(DataElement, status: ElementStatus.DRAFT)          == 5L
//        elementService.count(Asset, status: 'DRAFT')                            == 5L
//        elementService.count(Asset, status: ElementStatus.DRAFT)                == 5L
//    }


    def "create new version"() {
        DataElement author = DataElement.findByName('auth5')
        DataType domain = DataType.findByName('test1')

        DataModel dataModel = new DataModel(name: "auth5test1", semanticVersion: "1.0.0", status: ElementStatus.FINALIZED).save(failOnError: true)

        domain.dataModel = dataModel
        domain.save(failOnError: true)

        author.ext.something = 'anything'
        author.dataType = domain
        author.dataModel = dataModel
        author.save(failOnError: true)

        int originalVersion     = author.versionNumber
        DataElement draft       = elementService.createDraftVersion(author, DraftContext.userFriendly().forceNew()) as DataElement
        int draftVersion        = draft.versionNumber
        int newVersion          = author.versionNumber
        author.refresh()

        expect:
        author != draft
        author.id != draft.id
        author.versionCreated != draft.versionCreated
        originalVersion == newVersion
        draftVersion    == originalVersion + 1

        draft.ext.something == 'anything'

        draft.supersedes.contains(author)

        author.dataType
        draft.dataType

        author.status == ElementStatus.FINALIZED
        draft.status == ElementStatus.DRAFT

        when:
        def anotherDraft = elementService.createDraftVersion(draft, DraftContext.userFriendly().forceNew())

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
        source.dataModel
        destination.dataModel
        source.archived
        destination.supersededBy.contains source
        !m3cosd.archived
        m3sact.archived

        cleanup:
        source?.ext?.clear()
        destination?.ext?.clear()
        source?.deleteRelationships()
        source?.delete()
        destination?.deleteRelationships()
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
        DataModel dataModel = new DataModel(name: "cnvohm", semanticVersion: '1.0.0').save(failOnError: true)
        DataClass md1      = new DataClass(name:"test1", dataModel: dataModel).save(failOnError: true)
        DataClass md2      = new DataClass(name:"test2", dataModel: dataModel).save(failOnError: true)
        DataClass md3      = new DataClass(name:"test3", dataModel: dataModel).save(failOnError: true)

        md1.addToParentOf(md2)
        md2.addToParentOf(md3)

        elementService.finalizeElement(dataModel)

        DraftContext context = DraftContext.userFriendly()

        int originalVersion     = md2.versionNumber
        DataClass draft         = elementService.createDraftVersion(md2, context) as DataClass
        int draftVersion        = draft.versionNumber
        int newVersion          = md2.versionNumber

        List<Relationship> outgoingDraftRelationships = relationshipService.getRelationships([:], RelationshipDirection.OUTGOING, draft, RelationshipType.hierarchyType).items
        List<Relationship> incomingDraftRelationships = relationshipService.getRelationships([:], RelationshipDirection.INCOMING, draft, RelationshipType.hierarchyType).items

        expect:
        md2 != draft
        md2.id != draft.id
        originalVersion == newVersion
        draftVersion == originalVersion + 1

        draft.supersedes.contains(md2)

        when:
        CatalogueElement md1Draft = context.resolve(md1)
        CatalogueElement md3Draft = context.resolve(md3)

        then:
        md2.parentOf.contains(md3)
        md2.childOf.contains(md1)
        md2.parentOf.contains(md3)
        !md1.parentOf.contains(draft)
        draft.parentOf.contains(md3Draft)
        draft.childOf.contains(md1Draft)
        outgoingDraftRelationships.size() == 1
        incomingDraftRelationships.size() == 1

        cleanup:
        md1.delete()
        md2.delete()
        md3.delete()
    }

    def "finalize element"() {
        when:
        DataElement author = DataElement.findByName('auth5')
        author.dataModel = new DataModel(name: "fe", semanticVersion: "1.0.0", status: ElementStatus.FINALIZED).save(failOnError: true)
        author.save(failOnError: true)
        DataElement draft = elementService.createDraftVersion(author, DraftContext.userFriendly()) as DataElement

        then:
        draft.status    == ElementStatus.DRAFT
        author.status   == ElementStatus.FINALIZED

        elementService.finalizeElement(draft)

        then:
        draft.status    == ElementStatus.FINALIZED
        author.status   == ElementStatus.DEPRECATED
    }

    def "finalize tree"() {

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

    def "finalize tree infinite loop"() {
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
        DataModel dataModel = new DataModel(name: "mattnd", semanticVersion: "1.0.0", status: ElementStatus.FINALIZED).save(failOnError: true)
        DataType d1 = new DataType(name: "VD4MT1", status: ElementStatus.FINALIZED, dataModel: dataModel).save(failOnError: true, flush: true)
        DataType d2 = new DataType(name: "VD4MT2", status: ElementStatus.FINALIZED, dataModel: dataModel).save(failOnError: true, flush: true)

        Mapping mapping = mappingService.map(d1, d2, "x")

        expect:
        mapping.errors.errorCount == 0

        when:
        DraftContext context = DraftContext.userFriendly()
        elementService.createDraftVersion(dataModel, '1.0.1', context)
        DataType d1draft = context.resolve(d1) as DataType
        DataType d2draft = context.resolve(d2) as DataType

        then:
        d1draft
        d1draft.outgoingMappings
        d1draft.outgoingMappings.size() == 1
        d1draft.outgoingMappings[0].destination == d2draft
    }

    def "draft elements has the draft model I"() {
        final String dataModelName = "DM4DMA"
        final String dataTypeName = "DT4DMA"
        catalogueBuilder.build {
            skip draft
            dataModel name: dataModelName, {
                dataType name: dataTypeName
            }
        }

        DataModel source = DataModel.findByName(dataModelName)

        expect:
        source
        source.status == ElementStatus.DRAFT


        when:
        DataModel finalized = elementService.finalizeElement(source)
        DataType finalizedType = DataType.findByName(dataTypeName)

        then:
        finalized
        finalized.status == ElementStatus.FINALIZED
        source == finalized

        finalizedType.dataModel == source

        when:
        DataModel draft = elementService.createDraftVersion(finalized, DraftContext.userFriendly())
        DataType draftType = DataType.findByNameAndStatus(dataTypeName, ElementStatus.DRAFT)

        then:
        draft
        draft != finalized

        draftType
        draftType.dataModel == draft
    }

    def "draft elements has the draft model II"() {
        final String dataModelName = "DM4DMA2"
        final String dataTypeName = "DT4DMA2"
        catalogueBuilder.build {
            skip draft
            dataModel name: dataModelName, {
                dataType name: dataTypeName
            }
        }

        DataModel source = DataModel.findByName(dataModelName)

        expect:
        source
        source.status == ElementStatus.DRAFT


        when:
        DataModel finalized = elementService.finalizeElement(source)
        DataType finalizedType = DataType.findByName(dataTypeName)

        then:
        finalized
        finalized.status == ElementStatus.FINALIZED
        source == finalized

        finalizedType.dataModel == source

        when:
        DataType draftType = elementService.createDraftVersion(finalizedType, DraftContext.userFriendly())
        DataModel draft = DataModel.findByNameAndStatus(dataModelName, ElementStatus.DRAFT)

        then:
        draft
        draft != finalized

        draftType
        draftType.dataModel == draft

        draftType.findPreviousVersion() == finalizedType
    }

    @Unroll
    def "can change data type type when creating new draft to #type"() {
        DataType d1 = new DataType(name: "DT4CDT").save(failOnError: true, flush: true)
        DataElement element = new DataElement(name: 'DE4MET-732', dataType: d1).save(failOnError: true, flush: true)

        expect:
        element in d1.relatedDataElements

        when:
        DataType d1draft = elementService.createDraftVersion(d1, DraftContext.userFriendly().changeType(d1, type))

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
        DataType dataType = new DataType(name: 'VD4MET-732', status: ElementStatus.FINALIZED, dataModel: new DataModel(name: 'MET-732', semanticVersion: '0.0.1', status: ElementStatus.FINALIZED).save(failOnError: true)).save(failOnError: true, flush: true)

        dataType = elementService.createDraftVersion(dataType, DraftContext.importFriendly([] as Set)) as DataType

        expect:
        dataType

        when:
        dataType = elementService.finalizeElement(dataType)

        then:
        dataType

        when:
        dataType = elementService.archive(dataType, false)

        elementService.restore(dataType)

        then:
        dataType.status == dataType.dataModel.status
    }

    def "creating draft of data model won't set previous version as a data model"() {
        DataModel firstModel = new DataModel(name: "DataModel 4 DataModel DRAFT", status: ElementStatus.FINALIZED).save(failOnError: true, flush: true)

        when:
        DataModel newModel = elementService.createDraftVersion(firstModel, DraftContext.userFriendly())

        then:
        newModel != firstModel
        newModel.dataModel != firstModel
        newModel.dataModel == null
    }


    def "clone whole data model"() {
        final String dataTypeName = 'DT SM'
        final String sourceName = 'Source Model'
        final String anotherName = 'Another Model'
        final String anotherTypeName = 'Another Type'
        final String destinationName = 'Destination'
        final String dataElementName = 'Data Element'

        catalogueBuilder.build {
            skip draft
            dataModel name: anotherName, {
                dataType name: anotherTypeName
            }
            dataModel name: sourceName, {
                dataType name: dataTypeName
                dataElement name: dataElementName, {
                    dataType name: anotherTypeName, dataModel: anotherName
                }
            }
            dataModel name: destinationName
        }


        DataModel source = DataModel.findByName(sourceName)
        DataModel destination = DataModel.findByName(destinationName)
        DataModel another = DataModel.findByName(anotherName)

        when:
        DataType original = DataType.findByName(dataTypeName)
        DataModel stillDestination = elementService.cloneElement(source, CloningContext.create(source, destination))
        //DataType clone = DataType.findByNameAndDataModel(dataTypeName, destination)
        DataType clone = elementService.cloneElement(original, CloningContext.create(source, destination))

        then:
        destination == stillDestination
        verifyCloned source, destination, original, clone

        another
        destination.imports
//        another in destination.imports
    }

    def "clone data type"() {
        DataModel source = new DataModel(name: 'Source Model').save(failOnError: true)
        DataModel destination = new DataModel(name: 'Destination').save(failOnError: true)

        when:
        DataType original = new DataType(dataModel: source, name: 'DT SM')
        DataType clone = elementService.cloneElement(original, CloningContext.create(source, destination))

        then:
        verifyCloned source, destination, original, clone
    }

    def "clone data element"() {
        final String sourceModelName = 'Source Model 2'
        final String destinationModelName = 'Destination Model 2'
        final String originalDataElementName = 'DE CDE'
        final String originalDataTypeName = 'DT CDE'

        when:
        catalogueBuilder.build {
            skip draft
            dataModel(name: sourceModelName) {
                dataElement name: originalDataElementName, {
                    dataType name: originalDataTypeName, enumerations: [
                            one: '1',
                            two: '2'
                    ]
                }
            }
            dataModel name: destinationModelName
        }

        DataModel source = DataModel.findByName(sourceModelName)
        DataModel destination = DataModel.findByName(destinationModelName)

        DataElement originalDataElement = DataElement.findByName(originalDataElementName)
        DataType originalDataType = DataType.findByName(originalDataTypeName)

        then:
        source
        destination
        originalDataElement
        originalDataType

        originalDataType.instanceOf EnumeratedType

        originalDataElement.dataModel == source
        originalDataType.dataModel == source
        originalDataElement.dataType == originalDataType

        when:
        DataElement clonedDataElement = elementService.cloneElement(originalDataElement, CloningContext.create(source, destination))
       // DataType clonedDataType = DataType.findByNameAndDataModel(originalDataTypeName, destination)
        DataType clonedDataType = elementService.cloneElement(originalDataType, CloningContext.create(source, destination))

        then:
        clonedDataType.instanceOf EnumeratedType

        verifyCloned source, destination, originalDataElement, clonedDataElement
        verifyCloned source, destination, originalDataType, clonedDataType

        clonedDataElement.dataType == clonedDataType
    }

    def "clone data class"() {
        final String sourceModelName = 'Source Model 3'
        final String destinationModelName = 'Destination Model 3'
        final String originalDataClassName = 'DC CDC'
        final String childDataClassName = 'DC CDC II'
        final String grandChildDataClassName = 'DC CDC III'
        final String originalDataElementName = 'DE CDC'
        final String originalDataTypeName = 'DT CDC'

        when:
        catalogueBuilder.build {
            skip draft
            dataModel(name: 'Sea Otter') {
                dataClass name: 'DC CDC IV - other'
            }
            dataModel(name: sourceModelName) {
                dataClass name: originalDataClassName, {
                    dataElement name: originalDataElementName, {
                        dataType name: originalDataTypeName, enumerations: [one: '1', two: '2']
                    }
                    dataClass name: childDataClassName, {
                        dataClass name: grandChildDataClassName, {
                            dataClass name: 'DC CDC IV - other', dataModel: 'Sea Otter'
                        }
                    }
                }
            }
            dataModel name: destinationModelName
        }

        DataModel source = DataModel.findByName(sourceModelName)
        DataModel destination = DataModel.findByName(destinationModelName)
        DataModel other = DataModel.findByName('Sea Otter')

        DataClass originalDataClass = DataClass.findByName(originalDataClassName)
        DataClass childDataClass = DataClass.findByName(childDataClassName)
        DataClass grandChildDataClass = DataClass.findByName(grandChildDataClassName)
        DataClass otherDataClass = DataClass.findByName('DC CDC IV - other')
        DataElement originalDataElement = DataElement.findByName(originalDataElementName)
        DataType originalDataType = DataType.findByName(originalDataTypeName)

        then:
        source
        destination
        other
        originalDataClass
        childDataClass
        grandChildDataClass
        originalDataElement
        originalDataType
        otherDataClass

        originalDataType instanceof EnumeratedType

        originalDataClass.dataModel == source
        childDataClass.dataModel == source
        grandChildDataClass.dataModel == source
        originalDataElement.dataModel == source
        originalDataType.dataModel == source
        otherDataClass.dataModel == other

        originalDataElement in originalDataClass.contains
        originalDataElement.dataType == originalDataType

        when:
        DataClass clonedDataClass = elementService.cloneElement(originalDataClass, CloningContext.create(source, destination))
        //DataClass clonedChildDataClass = DataClass.findByNameAndDataModel(childDataClassName, destination)
        DataClass clonedChildDataClass = elementService.cloneElement(childDataClass, CloningContext.create(source, destination))
        //DataClass clonedGrandChildDataClass = DataClass.findByNameAndDataModel(grandChildDataClassName, destination)
        DataClass clonedGrandChildDataClass = elementService.cloneElement(grandChildDataClass, CloningContext.create(source, destination))

      //  DataElement clonedDataElement = DataElement.findByNameAndDataModel(originalDataElementName, destination)
       DataElement clonedDataElement = elementService.cloneElement(originalDataElement, CloningContext.create(source, destination))
        //DataType clonedDataType = DataType.findByNameAndDataModel(originalDataTypeName, destination)
        DataType clonedDataType = elementService.cloneElement(originalDataType, CloningContext.create(source, destination))

        then:
        clonedDataType instanceof EnumeratedType

        verifyCloned source, destination, originalDataClass, clonedDataClass
        verifyCloned source, destination, childDataClass, clonedChildDataClass
        verifyCloned source, destination, grandChildDataClass, clonedGrandChildDataClass
        verifyCloned source, destination, originalDataElement, clonedDataElement
        verifyCloned source, destination, originalDataType, clonedDataType

       // clonedDataElement in clonedDataClass.contains
       // clonedChildDataClass in clonedDataClass.parentOf
       // clonedGrandChildDataClass in clonedChildDataClass.parentOf
       // otherDataClass in clonedGrandChildDataClass.parentOf
        clonedDataElement.dataType == clonedDataType
    }


    def "cloning nested first than parent class crates duplicates"() {
        final String sourceDataModelName = 'Source DM MET-922'
        final String rootDataClassName = 'DC Root MET-922'
        final String parentDataClassName = 'DC Parent MET-922'
        final String destinationDataModelName = 'Destination DM MET-922'

        catalogueBuilder.build {
            skip draft
            dataModel name: sourceDataModelName, {
                dataClass name: rootDataClassName, {
                    dataClass name: parentDataClassName, {
                        5.times { i ->
                            dataClass name: "DC MET-922 #$i", {
                                5.times { j ->
                                    dataElement name: "DE MET-922 #$i/$j"
                                }
                            }
                        }
                    }
                }
            }

            dataModel name: destinationDataModelName
        }

        DataModel source = DataModel.findByName(sourceDataModelName)
        DataModel destination = DataModel.findByName(destinationDataModelName)

        DataClass root = DataClass.findByName(rootDataClassName)
        DataClass parent = DataClass.findByName(parentDataClassName)

        expect:
        source
        destination
        root
        parent

        DataElement.countByNameLike("DE MET-922%") == 25

        when:
        elementService.cloneElement(parent, CloningContext.create(source, destination))

        then:
        DataClass.findByNameAndDataModel(parentDataClassName, source)
        DataClass.findByNameAndDataModel(parentDataClassName, destination)

        DataElement.countByNameLike("DE MET-922%") == 25

        when:
        elementService.cloneElement(root, CloningContext.create(source, destination))

        then:
        DataClass.findByNameAndDataModel(rootDataClassName, source)
        DataClass.findByNameAndDataModel(rootDataClassName, destination)

        DataElement.countByNameLike("DE MET-922%") == 25
    }


    def "find by model catalogue id"() {
        final String dataModelName = "FBMCIITII Data Model"
        final String dataModelSemVer = "release/1.0.0"
        final String orphanDataTypeName = "FBMCIITII Orpan Data Type"
        final String orphanModelCatalogueId = "http://www.example.com/types/FBMCIITII_Orphan_Data_Type"
        final String dataTypeName = "FBMCIITII Data Type"
        final String dataTypeModelCatalogueId = "http://www.example.com/types/FBMCIITII"
        final String anotherDataModelName = "testDM2"

        catalogueBuilder.build {
            skip draft
            dataModel name: dataModelName, semanticVersion: dataModelSemVer, {
                dataType name: dataTypeName, id: dataTypeModelCatalogueId
            }
            dataModel name: anotherDataModelName
            dataType name: orphanDataTypeName, id: orphanModelCatalogueId
        }

        DataModel anotherDataModel = DataModel.findByName(anotherDataModelName)
        elementService.finalizeDataModel(anotherDataModel, "0.0.1", "new model", true)


        expect:
        anotherDataModel.errors.errorCount == 0

        when:
        DataModel anotherDataModelDraft = elementService.createDraftVersion(anotherDataModel, '0.0.2', DraftContext.userFriendly())

        final String dataModelModelCatalogueId = "catalogue/dataModel/$anotherDataModelDraft.latestVersionId@0.0.2"

        CatalogueElement anotherDataModelFound = elementService.findByModelCatalogueId(CatalogueElement, dataModelModelCatalogueId)


        then:

        anotherDataModelFound.id == anotherDataModelDraft.id


        when:
        DataModel dataModel = DataModel.findByName(dataModelName)
        DataType dataTypeV1 = DataType.findByName(dataTypeName)
        elementService.finalizeDataModel(dataModel, "0.0.1", "new model", true)

        then:
        dataModel.errors.errorCount == 0

        when:
        DataModel dataModelV2 = elementService.createDraftVersion(dataModel, '0.0.2', DraftContext.userFriendly())

        CatalogueElement dataType = elementService.findByModelCatalogueId(CatalogueElement, dataTypeModelCatalogueId)

        elementService.finalizeDataModel(dataModelV2, "0.0.2", "new model", true)
        then:
        dataModelV2.errors.errorCount == 0

        when:
        elementService.createDraftVersion(dataModelV2, '0.0.3', DraftContext.userFriendly())

        then:
        dataTypeV1
        dataTypeV1 != dataType

        dataType
        dataType.dataModel
        dataType.dataModel.semanticVersion
        dataType.dataModel.semanticVersion == '0.0.2'
        dataType.name == dataTypeName

        when:
        CatalogueElement withVersion = elementService.findByModelCatalogueId(CatalogueElement, dataType.getDefaultModelCatalogueId(false))

        then:
        withVersion
        withVersion == dataType

        when:
        CatalogueElement withoutVersion = elementService.findByModelCatalogueId(CatalogueElement, dataType.getDefaultModelCatalogueId(true))

        then:
        withoutVersion
        withoutVersion.dataModel
        withoutVersion.dataModel.semanticVersion == '0.0.3'

        when:
        CatalogueElement legacy = elementService.findByModelCatalogueId(CatalogueElement, dataType.getLegacyModelCatalogueId(false))

        then:
        legacy
        legacy == dataType

        when:
        CatalogueElement orphan = elementService.findByModelCatalogueId(CatalogueElement, orphanModelCatalogueId)

        then:
        orphan
        orphan.name == orphanDataTypeName

        when:
        CatalogueElement orphanWithVersion = elementService.findByModelCatalogueId(CatalogueElement, orphan.getDefaultModelCatalogueId(true))

        then:
        orphanWithVersion
        orphanWithVersion == orphan

        when:
        CatalogueElement legacyWithVersion = elementService.findByModelCatalogueId(CatalogueElement, orphan.getLegacyModelCatalogueId(false))

        then:
        legacyWithVersion
        legacyWithVersion == orphan

        when:
        CatalogueElement orphanWithoutVersion = elementService.findByModelCatalogueId(CatalogueElement, orphan.getDefaultModelCatalogueId(false))

        then:
        orphanWithoutVersion
        orphanWithoutVersion == orphan

    }

    private static <E extends CatalogueElement> boolean verifyCloned(DataModel source, DataModel destination, E original, E clone) {
        assert clone
        assert clone.errors.errorCount == 0

        assert original != clone

        assert original.dataModel == source
        assert original.status == ElementStatus.DRAFT

        assert clone.dataModel == destination
        assert clone.status == ElementStatus.DRAFT

        assert clone.latestVersionId
        assert clone.latestVersionId != original.latestVersionId
        assert clone.latestVersionId != original.id

        assert original.name == clone.name
        assert original.description == clone.description
        assert original.ext == clone.ext

        return true
    }


    def "get type hierarchy"() {
        catalogueBuilder.build {
            skip draft
            dataModel name: 'Data Model for Testing Type Hierarchy', {
                dataType name: 'L3', {
                    dataType name: 'L2', {
                        dataType name: 'L1'
                    }
                }
            }
        }

        DataType l1 = DataType.findByName('L1')
        DataType l2 = DataType.findByName('L2')
        DataType l3 = DataType.findByName('L3')

        expect:
        l1
        l2
        l3
        l1 in l2.isBasedOn
        l2 in l3.isBasedOn

        when:
        ListWithTotalAndType<DataType> typeHierarchy = elementService.getTypeHierarchy([:], l3)

        then:
        typeHierarchy.total == 2L
        typeHierarchy.items[0] == l2
        typeHierarchy.items[1] == l1
    }

    def "change type"() {
        given:
            DataModel dataModel = new DataModel(name: 'Test Change Type').save(failOnError: true)
            EnumeratedType type = new EnumeratedType(name: 'Test Change Type Enum', enumerations: [a: 'b'], dataModel: dataModel).save(failOnError: true)
            DataElement element = new DataElement(name: 'Data Element for Testing Change Type', dataModel: dataModel, dataType: type).save(failOnError: true)
        expect:
            type
            type.instanceOf(EnumeratedType)
            type.dataModel == dataModel
            element.dataType == type
        when:
            PrimitiveType primitiveType = elementService.changeType(type, PrimitiveType)
        then:
            primitiveType
            primitiveType.instanceOf(PrimitiveType)
            primitiveType.dataModel == dataModel
            type.status == ElementStatus.DEPRECATED
            element.dataType == primitiveType
    }

    def "keep the elements deprecated when creating new version"() {
        DataModel model = new DataModel(name: "Test Keep Deprecated", status: ElementStatus.FINALIZED, semanticVersion: "1").save(failOnError: true)
        DataClass dataClass = new DataClass(name: "Test Class", dataModel: model, status: ElementStatus.DEPRECATED).save(failOnError: true)

        DraftContext context = DraftContext.userFriendly()

        DataModel v2 = elementService.createDraftVersion(model, "2", context)

        DataClass dcV2 = DataClass.findByNameAndDataModel(dataClass.name, v2)

        expect:
        v2
        dcV2
        dcV2.status == ElementStatus.DEPRECATED

    }

}
