package org.modelcatalogue.core.audit

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.ElementStatus
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.publishing.DraftContext

class AuditingIntegrationSpec extends IntegrationSpec {

    def elementService
    def initCatalogueService
    def sessionFactory

    def "creation of new element is logged"() {
        when:
        DataType type = new DataType(name: "DT4CL").save(failOnError: true)
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


    def "creation of draft element is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()
        when:
        DataType vOne = new DataType(name: "DT4DCL").save(failOnError: true)
        DataType type = vOne.publish(elementService).createDraftVersion(elementService, DraftContext.userFriendly())
        Change change = Change.findByChangedId(type.id)

        then:
        change
        change.changedId == type.id
        change.latestVersionId == vOne.id
        change.type == ChangeType.NEW_VERSION_CREATED
        change.authorId == null
        change.property == null
        change.newValue == null
        change.oldValue == null
    }

    def "finalization is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()
        when:
        DataType vOne = new DataType(name: "DT4DEF").save(flush: true, failOnError: true)
        vOne.status = ElementStatus.FINALIZED
        vOne.save(flush: true, failOnError: true)

        Change change = Change.findByChangedIdAndType(vOne.id, ChangeType.ELEMENT_FINALIZED)

        then:
        change
        change.changedId == vOne.id
        change.latestVersionId == vOne.id
        change.authorId == null
        change.property == 'status'
        change.newValue == AuditService.storeValue(ElementStatus.FINALIZED)
        change.oldValue == AuditService.storeValue(ElementStatus.DRAFT)
    }

    def "deprecation is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()
        when:
        DataType vOne = new DataType(name: "DT4DEF").save(flush: true, failOnError: true)
        vOne.status = ElementStatus.DEPRECATED
        vOne.save(flush: true, failOnError: true)

        Change change = Change.findByChangedIdAndType(vOne.id, ChangeType.ELEMENT_DEPRECATED)

        then:
        change
        change.changedId == vOne.id
        change.latestVersionId == vOne.id
        change.authorId == null
        change.property == 'status'
        change.newValue == AuditService.storeValue(ElementStatus.DEPRECATED)
        change.oldValue == AuditService.storeValue(ElementStatus.DRAFT)
    }

    def "valid updating property is logged"() {
        String original = "The Data Type for Update Log"
        String changed  = "The Data Type for Update Log - Changed"

        when:
        DataType type = new DataType(name: "DT4ULV", description: original).save(failOnError: true, flush: true)
        type.description = changed
        type.save(failOnError: true, flush: true)
        Change change = Change.findByChangedIdAndProperty(type.id, 'description')

        then:
        change
        change.changedId == type.id
        change.latestVersionId == type.id
        change.type == ChangeType.PROPERTY_CHANGED
        change.authorId == null
        change.property == 'description'
        change.newValue == AuditService.storeValue(changed)
        change.oldValue == AuditService.storeValue(original)
    }

    def "invalid updating property is not logged"() {
        String original = "The Data Type for Update Log"
        String changed  = 'x' * 3000

        when:
        DataType type = new DataType(name: "DT4ULI", description: original).save(failOnError: true, flush: true)
        type.description = changed
        type.save()
        Change change = Change.findByChangedIdAndProperty(type.id, 'description')

        then:
        type.hasErrors()
        !change
    }

    def "deleting element is logged"() {
        when:
        DataType type = new DataType(name: "DT4CL").save(failOnError: true, flush: true)

        String expectedOldValue = AuditService.storeValue(type)

        type.delete(flush: true)
        Change change = Change.findByChangedIdAndType(type.id, ChangeType.ELEMENT_DELETED)

        then:
        change
        change.latestVersionId == type.id
        change.authorId == null
        change.property == null
        change.newValue == null
        change.oldValue == expectedOldValue
    }

    def "adding new metadata is logged"() {
        DataType type = new DataType(name: 'DT4ANM').save(failOnError: true, flush: true)
        ExtensionValue ext = new ExtensionValue(name: 'foo', extensionValue: 'bar', element: type).save(failOnError: true, flush: true)
        type.refresh()

        Change change = Change.findByChangedIdAndType(type.id, ChangeType.METADATA_CREATED)

        expect:
        type.ext.foo == ext.extensionValue

        change
        change.latestVersionId == type.id
        change.authorId   == null
        change.property == 'foo'
        change.newValue == AuditService.storeValue('bar')
        change.oldValue == null

    }

    def "editing metadata is logged"() {
        DataType type = new DataType(name: 'DT4ANM').save(failOnError: true, flush: true)
        ExtensionValue ext = new ExtensionValue(name: 'foo', extensionValue: 'bar', element: type).save(failOnError: true, flush: true)
        ext.extensionValue = 'boo'
        ext.save(failOnError: true, flush: true)
        type.refresh()

        Change change = Change.findByChangedIdAndType(type.id, ChangeType.METADATA_UPDATED)

        expect:
        type.ext.foo == ext.extensionValue

        change
        change.latestVersionId == type.id
        change.authorId   == null
        change.property == 'foo'
        change.oldValue == AuditService.storeValue('bar')
        change.newValue == AuditService.storeValue('boo')
    }

    def "deleting metadata is logged"() {
        DataType type = new DataType(name: 'DT4ANM').save(failOnError: true, flush: true)
        
        type.ext.foo = 'bar'
        sessionFactory.currentSession.flush()


        type.ext.remove('foo')
        sessionFactory.currentSession.flush()

        Change change = Change.findByChangedIdAndType(type.id, ChangeType.METADATA_DELETED)

        expect:
        !type.ext.foo

        change
        change.latestVersionId == type.id
        change.authorId   == null
        change.property == 'foo'
        change.oldValue == AuditService.storeValue('bar')
        change.newValue == null
    }

    def "adding relationship is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()

        DataType type = new DataType(name: 'DT4ANR TYPE').save(failOnError: true, flush: true)
        DataType base = new DataType(name: 'DT4ANR BASE').save(failOnError: true, flush: true)

        type.addToIsBasedOn base

        Change change1 = Change.findByChangedIdAndType(type.id, ChangeType.RELATIONSHIP_CREATED)
        Change change2 = Change.findByChangedIdAndType(base.id, ChangeType.RELATIONSHIP_CREATED)


        expect:
        base in type.isBasedOn
        type in base.isBaseFor

        change1
        change1.latestVersionId == type.id
        change1.authorId   == null
        change1.property == 'is based on'
        change1.newValue == AuditService.storeValue(base)
        change1.oldValue == null

        change2
        change2.latestVersionId == base.id
        change2.authorId   == null
        change2.property == 'is base for'
        change2.newValue == AuditService.storeValue(type)
        change2.oldValue == null
    }

    def "removing relationship is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()

        DataType type = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)
        DataType base = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)

        type.addToIsBasedOn base
        type.removeFromIsBasedOn base

        Change change1 = Change.findByChangedIdAndType(type.id, ChangeType.RELATIONSHIP_DELETED)
        Change change2 = Change.findByChangedIdAndType(base.id, ChangeType.RELATIONSHIP_DELETED)


        expect:
        !(base in type.isBasedOn)
        !(type in base.isBaseFor)

        change1
        change1.latestVersionId == type.id
        change1.authorId   == null
        change1.property == 'is based on'
        change1.newValue == AuditService.storeValue(base)
        change1.oldValue == null

        change2
        change2.latestVersionId == base.id
        change2.authorId   == null
        change2.property == 'is base for'
        change2.newValue == AuditService.storeValue(type)
        change2.oldValue == null
    }

    def "adding relationship metadata is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()

        DataType type = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)
        DataType base = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)

        Relationship relationship = type.addToIsBasedOn base

        relationship.ext.foo = 'bar'

        Change change1 = Change.findByChangedIdAndType(type.id, ChangeType.RELATIONSHIP_METADATA_CREATED)
        Change change2 = Change.findByChangedIdAndType(base.id, ChangeType.RELATIONSHIP_METADATA_CREATED)


        expect:
        change1
        change1.latestVersionId == type.id
        change1.authorId   == null
        change1.property == 'is based on [foo]'
        change1.newValue == AuditService.storeValue('bar')
        change1.oldValue == null

        change2
        change2.latestVersionId == base.id
        change2.authorId   == null
        change2.property == 'is base for [foo]'
        change2.newValue == AuditService.storeValue('bar')
        change2.oldValue == null
    }

    def "editing relationship metadata is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()

        DataType type = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)
        DataType base = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)

        Relationship relationship = type.addToIsBasedOn base

        relationship.ext.foo = 'bar'
        sessionFactory.currentSession.flush()

        relationship.ext.foo = 'baz'
        sessionFactory.currentSession.flush()

        Change change1 = Change.findByChangedIdAndType(type.id, ChangeType.RELATIONSHIP_METADATA_UPDATED)
        Change change2 = Change.findByChangedIdAndType(base.id, ChangeType.RELATIONSHIP_METADATA_UPDATED)

        expect:
        change1
        change1.latestVersionId == type.id
        change1.authorId   == null
        change1.property == 'is based on [foo]'
        change1.newValue == AuditService.storeValue('baz')
        change1.oldValue == AuditService.storeValue('bar')

        change2
        change2.latestVersionId == base.id
        change2.authorId   == null
        change2.property == 'is base for [foo]'
        change2.newValue == AuditService.storeValue('baz')
        change2.oldValue == AuditService.storeValue('bar')
    }

    def "deleting relationship metadata is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()

        DataType type = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)
        DataType base = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)

        Relationship relationship = type.addToIsBasedOn base

        relationship.ext.foo = 'bar'
        sessionFactory.currentSession.flush()

        relationship.ext.remove('foo')
        sessionFactory.currentSession.flush()

        Change change1 = Change.findByChangedIdAndType(type.id, ChangeType.RELATIONSHIP_METADATA_DELETED)
        Change change2 = Change.findByChangedIdAndType(base.id, ChangeType.RELATIONSHIP_METADATA_DELETED)

        expect:
        change1
        change1.latestVersionId == type.id
        change1.authorId   == null
        change1.property == 'is based on [foo]'
        change1.newValue == null
        change1.oldValue == AuditService.storeValue('bar')

        change2
        change2.latestVersionId == base.id
        change2.authorId   == null
        change2.property == 'is base for [foo]'
        change2.newValue == null
        change2.oldValue == AuditService.storeValue('bar')
    }

    def "creating draft is ignored as it is already logged as creating new version"() {
        DataType type = new DataType(name: 'DT4DSI', status: ElementStatus.FINALIZED).save(failOnError: true, flush: true)

        type.status = ElementStatus.DRAFT
        type.save(failOnError: true, flush: true)

        expect:
        !Change.findByTypeAndChangedIdAndProperty(ChangeType.PROPERTY_CHANGED, type.id, 'status')

    }

}
