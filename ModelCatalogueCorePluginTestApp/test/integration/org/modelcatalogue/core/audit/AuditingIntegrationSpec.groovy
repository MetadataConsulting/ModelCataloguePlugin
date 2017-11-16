package org.modelcatalogue.core.audit

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.builder.ProgressMonitor
import spock.lang.Ignore


class AuditingIntegrationSpec extends IntegrationSpec {

    def elementService
    def initCatalogueService
    def sessionFactory
    def mappingService
    def auditService


    def "creation of new element is logged"() {
        when:
        DataType type = new DataType(name: "DT4CL").save(failOnError: true)

        Thread.sleep(100)

        Change change = Change.findByChangedId(type.id)

        then:
        change
        change.latestVersionId == type.id
        change.type == ChangeType.NEW_ELEMENT_CREATED
        change.authorId == null
        change.property == null
        change.newValue == null
        change.oldValue == null
    }

    @Ignore
    def "creation of draft element is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()
        DataModel dataModel = new DataModel(name: "codeil", status: ElementStatus.FINALIZED, semanticVersion: "1.0.0").save(failOnError: true)
        when:
        DataType vOne = new DataType(name: "DT4DCL", dataModel: dataModel, status: ElementStatus.FINALIZED).save(failOnError: true)
        elementService.createDraftVersion(vOne.publish(elementService, ProgressMonitor.NOOP), DraftContext.userFriendly()) as DataType

        Thread.sleep(100)

        Change change = Change.findByType(ChangeType.NEW_VERSION_CREATED)

        then:
        change
        change.latestVersionId == dataModel.id
        change.authorId != null
        change.property == null
        change.newValue == null
        change.oldValue == null
    }
    @Ignore
    def "finalization is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()
        when:
        DataType vOne = elementService.finalizeElement(new DataType(name: "DT4DEF").save(flush: true, failOnError: true))

        Thread.sleep(100)

        Change change = Change.findByChangedIdAndType(vOne.id, ChangeType.ELEMENT_FINALIZED)

        then:
        change
        change.changedId == vOne.id
        change.latestVersionId == vOne.id
        change.authorId != null
        change.property == 'status'
        change.newValue == LoggingAuditor.storeValue(ElementStatus.FINALIZED)
        change.oldValue == LoggingAuditor.storeValue(ElementStatus.DRAFT)
    }
    @Ignore
    def "deprecation is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()
        when:
        DataType vOne = new DataType(name: "DT4DEF").save(flush: true, failOnError: true)
        vOne = elementService.archive(vOne, true)
        vOne.save(flush: true, failOnError: true)

        Thread.sleep(100)

        Change change = Change.findByChangedIdAndType(vOne.id, ChangeType.ELEMENT_DEPRECATED)

        then:
        change
        change.changedId == vOne.id
        change.latestVersionId == vOne.id
        change.authorId != null
        change.property == 'status'
        change.newValue == LoggingAuditor.storeValue(ElementStatus.DEPRECATED)
        change.oldValue == LoggingAuditor.storeValue(ElementStatus.DRAFT)
    }
    @Ignore
    def "valid updating property is logged"() {
        String original = "The Data Type for Update Log"
        String changed  = "The Data Type for Update Log - Changed"

        when:
        DataType type = new DataType(name: "DT4ULV", description: original).save(failOnError: true, flush: true)
        type.description = changed
        type.save(failOnError: true, flush: true)

        Thread.sleep(100)

        Change change = Change.findByChangedIdAndProperty(type.id, 'description')

        then:
        change
        change.changedId == type.id
        change.latestVersionId == type.id
        change.type == ChangeType.PROPERTY_CHANGED
        change.authorId != null
        change.property == 'description'
        change.newValue
        change.oldValue

        expect:
        ChangeType.PROPERTY_CHANGED.undoSupported

        when:
        ChangeType.PROPERTY_CHANGED.undo(change)

        then:
        type.description == original
    }

    def "invalid updating property is not logged"() {
        String original = "The Data Type for Update Log"
        String changed  = 'x' * 20001

        when:
        DataType type = new DataType(name: "DT4ULI", description: original).save(failOnError: true, flush: true)
        type.description = changed
        type.save()

        Thread.sleep(100)

        Change change = Change.findByChangedIdAndProperty(type.id, 'description')

        then:
        type.hasErrors()
        !change
    }
    @Ignore
    def "deleting element is logged"() {
        when:
        DataType type = new DataType(name: "DT4CL").save(failOnError: true, flush: true)

        String expectedOldValue = LoggingAuditor.storeValue(type)

        type.delete(flush: true)

        Thread.sleep(100)

        Change change = Change.findByChangedIdAndType(type.id, ChangeType.ELEMENT_DELETED)

        then:
        change
        change.latestVersionId == type.id
        change.authorId != null
        change.property == null
        change.newValue == null
        change.oldValue == expectedOldValue
    }
    @Ignore
    def "adding new metadata is logged"() {
        DataType type = new DataType(name: 'DT4ANM').save(failOnError: true, flush: true)
        type.ext.foo = 'bar'

        Thread.sleep(100)

        Change change = Change.findByChangedIdAndType(type.id, ChangeType.METADATA_CREATED)

        expect:
        type.ext.foo == 'bar'

        change
        change.latestVersionId == type.id
        change.authorId != null
        change.property == 'foo'
        change.newValue
        change.oldValue == null

        and:
        ChangeType.METADATA_CREATED.undoSupported

        when:
        ChangeType.METADATA_CREATED.undo(change)

        then:
        type.ext.foo == null

    }
    @Ignore
    def "editing metadata is logged"() {
        DataType type = new DataType(name: 'DT4ANM').save(failOnError: true, flush: true)
        type.ext.foo = 'bar'
        type.ext.foo = 'boo'

        Thread.sleep(100)

        Change change = Change.findByChangedIdAndType(type.id, ChangeType.METADATA_UPDATED)

        expect:
        type.ext.foo == 'boo'

        change
        change.latestVersionId == type.id
        change.authorId != null
        change.property == 'foo'
        change.oldValue
        change.newValue

        and:
        ChangeType.METADATA_UPDATED.undoSupported

        when:
        ChangeType.METADATA_UPDATED.undo(change)

        then:
        type.ext.foo == 'bar'
    }
    @Ignore
    def "deleting metadata is logged"() {
        DataType type = new DataType(name: 'DT4ANM').save(failOnError: true, flush: true)

        type.ext.foo = 'bar'
        sessionFactory.currentSession.flush()


        type.ext.remove('foo')
        sessionFactory.currentSession.flush()

        Thread.sleep(100)

        Change change = Change.findByChangedIdAndType(type.id, ChangeType.METADATA_DELETED)

        expect:
        !type.ext.foo

        change
        change.latestVersionId == type.id
        change.authorId != null
        change.property == 'foo'
        change.oldValue == LoggingAuditor.storeValue('bar')
        change.newValue == null

        and:
        ChangeType.METADATA_DELETED.undoSupported

        when:
        ChangeType.METADATA_DELETED.undo(change)

        then:
        type.ext.foo == 'bar'
    }
    @Ignore
    def "adding relationship is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()

        DataType type = new DataType(name: 'DT4ANR TYPE').save(failOnError: true, flush: true)
        DataType base = new DataType(name: 'DT4ANR BASE').save(failOnError: true, flush: true)

        Relationship rel = type.addToIsBasedOn base

        Thread.sleep(100)

        Change change1 = Change.findByChangedIdAndType(type.id, ChangeType.RELATIONSHIP_CREATED)
        Change change2 = Change.findByChangedIdAndType(base.id, ChangeType.RELATIONSHIP_CREATED)


        expect:
        base in type.isBasedOn
        type in base.isBaseFor

        change1
        change1.latestVersionId == type.id
        change1.authorId != null
        change1.property == 'is based on'
        change1.newValue
        change1.oldValue == null

        change2
        change2.latestVersionId == base.id
        change2.authorId != null
        change2.property == 'is base for'
        change2.newValue
        change2.oldValue == null

        and:
        ChangeType.RELATIONSHIP_CREATED.undoSupported

        when:
        ChangeType.RELATIONSHIP_CREATED.undo(change1)

        then:
        !(base in type.isBasedOn)

    }
    @Ignore
    def "removing relationship is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()

        DataType type = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)
        DataType base = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)

        String initialValue = LoggingAuditor.storeValue(type.addToIsBasedOn(base))
        type.removeFromIsBasedOn base

        Thread.sleep(100)

        Change change1 = Change.findByChangedIdAndType(type.id, ChangeType.RELATIONSHIP_DELETED)
        Change change2 = Change.findByChangedIdAndType(base.id, ChangeType.RELATIONSHIP_DELETED)


        expect:
        !(base in type.isBasedOn)
        !(type in base.isBaseFor)

        change1
        change1.latestVersionId == type.id
        change1.authorId != null
        change1.property == 'is based on'
        change1.oldValue == initialValue
        change1.newValue == null
        !change1.otherSide

        change2
        change2.latestVersionId == base.id
        change2.authorId != null
        change2.property == 'is base for'
        change2.oldValue == initialValue
        change2.newValue == null
        change2.otherSide
    }
    @Ignore
    def "adding relationship metadata is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()

        DataType type = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)
        DataType base = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)

        Relationship relationship = type.addToIsBasedOn base

        relationship.ext.foo = 'bar'

        Thread.sleep(100)

        Change change1 = Change.findByChangedIdAndType(type.id, ChangeType.RELATIONSHIP_METADATA_CREATED)
        Change change2 = Change.findByChangedIdAndType(base.id, ChangeType.RELATIONSHIP_METADATA_CREATED)

        String newValue = LoggingAuditor.storeValue(new RelationshipMetadata(name: 'foo', extensionValue: 'bar', relationship: relationship))

        expect:
        change1
        change1.latestVersionId == type.id
        change1.authorId != null
        change1.property == 'is based on'
        change1.newValue == newValue
        change1.oldValue == null

        change2
        change2.latestVersionId == base.id
        change2.authorId != null
        change2.property == 'is base for'
        change2.newValue == newValue
        change2.oldValue == null

        and:
        ChangeType.RELATIONSHIP_METADATA_CREATED.undoSupported

        when:
        ChangeType.RELATIONSHIP_METADATA_CREATED.undo(change1)

        then:
        !relationship.ext.foo
    }
    @Ignore
    def "editing relationship metadata is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()

        DataType type = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)
        DataType base = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)

        Relationship relationship = type.addToIsBasedOn base

        relationship.ext.foo = 'bar'
        sessionFactory.currentSession.flush()

        relationship.ext.foo = 'baz'
        sessionFactory.currentSession.flush()


        Thread.sleep(100)

        Change change1 = Change.findByChangedIdAndType(type.id, ChangeType.RELATIONSHIP_METADATA_UPDATED)
        Change change2 = Change.findByChangedIdAndType(base.id, ChangeType.RELATIONSHIP_METADATA_UPDATED)

        expect:
        change1
        change1.latestVersionId == type.id
        change1.authorId != null
        change1.property == 'is based on'
        change1.newValue
        change1.oldValue == LoggingAuditor.storeValue('bar')

        change2
        change2.latestVersionId == base.id
        change2.authorId != null
        change2.property == 'is base for'
        change2.newValue
        change2.oldValue == LoggingAuditor.storeValue('bar')

        and:
        ChangeType.RELATIONSHIP_METADATA_UPDATED

        when:
        ChangeType.RELATIONSHIP_METADATA_UPDATED.undo(change1)

        then:
        relationship.ext.foo == 'bar'
    }
    @Ignore
    def "deleting relationship metadata is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()

        DataType type = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)
        DataType base = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)

        Relationship relationship = type.addToIsBasedOn base

        relationship.ext.foo = 'bar'
        sessionFactory.currentSession.flush()

        relationship.ext.remove('foo')
        sessionFactory.currentSession.flush()

        Thread.sleep(100)

        Change change1 = Change.findByChangedIdAndType(type.id, ChangeType.RELATIONSHIP_METADATA_DELETED)
        Change change2 = Change.findByChangedIdAndType(base.id, ChangeType.RELATIONSHIP_METADATA_DELETED)

        expect:
        change1
        change1.latestVersionId == type.id
        change1.authorId != null
        change1.property == 'is based on'
        change1.newValue == null
        change1.oldValue

        change2
        change2.latestVersionId == base.id
        change2.authorId != null
        change2.property == 'is base for'
        change2.newValue == null
        change2.oldValue

        and:
        ChangeType.RELATIONSHIP_METADATA_DELETED.undoSupported

        when:
        ChangeType.RELATIONSHIP_METADATA_DELETED.undo(change1)

        then:
        relationship.ext.foo == 'bar'
    }

    def "creating draft is ignored as it is already logged as creating new version"() {
        DataType type = new DataType(name: 'DT4DSI', status: ElementStatus.FINALIZED).save(failOnError: true, flush: true)

        type.status = ElementStatus.DRAFT
        type.save(failOnError: true, flush: true)

        Thread.sleep(100)

        expect:
        !Change.findByTypeAndChangedIdAndPropertyAndSystemNotEqual(ChangeType.PROPERTY_CHANGED, type.id, 'status', true)
    }

    def "auditing can be disabled"() {
        DataType type = auditService.mute {
             new DataType(name: 'DT4DIS', status: ElementStatus.FINALIZED).save(failOnError: true, flush: true)
        }

        Thread.sleep(100)

        expect:
        !Change.findByTypeAndChangedIdAndSystemNotEqual(ChangeType.NEW_ELEMENT_CREATED, type.id, true)
    }
    @Ignore
    def "creating mapping is logged"() {
        DataType type = new DataType(name: 'DT4ANM ONE').save(failOnError: true, flush: true)
        DataType base = new DataType(name: 'DT4ANM TWO').save(failOnError: true, flush: true)

        Mapping mapping = mappingService.map(type, base, "x / 2")

        Thread.sleep(100)

        Change change1 = Change.findByChangedIdAndType(type.id, ChangeType.MAPPING_CREATED)
        Change change2 = Change.findByChangedIdAndType(base.id, ChangeType.MAPPING_CREATED)


        expect:
        mapping.errors.errorCount == 0

        change1
        change1.latestVersionId == type.id
        change1.authorId != null
        change1.newValue
        change1.oldValue == null
        !change1.otherSide

        change2
        change2.latestVersionId == base.id
        change2.authorId != null
        change2.newValue
        change2.oldValue == null
        change2.otherSide

        and:
        ChangeType.MAPPING_CREATED.undoSupported
        type.outgoingMappings.size() == 1

        when:
        ChangeType.MAPPING_CREATED.undo(change1)

        then:
        type.outgoingMappings.size() == 0
    }
    @Ignore
    def "deleting mapping is logged"() {
        DataType type = new DataType(name: 'DT4RM ONE').save(failOnError: true, flush: true)
        DataType base = new DataType(name: 'DT4RM TWO').save(failOnError: true, flush: true)

        Mapping mapping = mappingService.map(type, base, "x / 2")
        mappingService.unmap(type, base)

        Thread.sleep(100)

        Change change1 = Change.findByChangedIdAndType(type.id, ChangeType.MAPPING_DELETED)
        Change change2 = Change.findByChangedIdAndType(base.id, ChangeType.MAPPING_DELETED)

        expect:
        change1
        change1.latestVersionId == type.id
        change1.authorId != null
        change1.newValue == null
        change1.oldValue
        !change1.otherSide

        change2
        change2.latestVersionId == base.id
        change2.authorId != null
        change2.newValue == null
        change2.oldValue
        change2.otherSide

        and:
        ChangeType.MAPPING_DELETED.undoSupported
        type.outgoingMappings.size() == 0

        when:
        ChangeType.MAPPING_DELETED.undo(change1)

        then:
        type.outgoingMappings.size() == 1
        type.outgoingMappings[0].destination == base
        type.outgoingMappings[0].mapping == mapping.mapping
    }

    @Ignore
    def "updating mapping is logged"() {
        DataType type = new DataType(name: 'DT4UM ONE').save(failOnError: true, flush: true)
        DataType base = new DataType(name: 'DT4UM TWO').save(failOnError: true, flush: true)

        Mapping mapping = mappingService.map(type, base, "x / 2")

        mapping = mappingService.map(type, base, "x / 3")

        Thread.sleep(100)

        Change change1 = Change.findByChangedIdAndType(type.id, ChangeType.MAPPING_UPDATED)
        Change change2 = Change.findByChangedIdAndType(base.id, ChangeType.MAPPING_UPDATED)

        expect:
        mapping.errors.errorCount == 0

        change1
        change1.latestVersionId == type.id
        change1.authorId != null
        change1.newValue
        change1.oldValue
        !change1.otherSide

        change2
        change2.latestVersionId == base.id
        change2.authorId != null
        change2.newValue
        change2.oldValue
        change2.otherSide

        and:
        ChangeType.MAPPING_UPDATED.undoSupported
        type.outgoingMappings.size() == 1

        when:
        ChangeType.MAPPING_UPDATED.undo(change1)

        then:
        type.outgoingMappings.size() == 1
        type.outgoingMappings[0].mapping == "x / 2"
    }

    def "some global changes are available"() {
        new DataType(name: "DT4GF").save(failOnError: true)

        Thread.sleep(100)

        expect:
        auditService.getGlobalChanges([:], DataModelFilter.NO_FILTER).total > 0
    }

}
