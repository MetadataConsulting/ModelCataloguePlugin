package org.modelcatalogue.core.audit

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.publishing.DraftContext

class AuditingIntegrationSpec extends IntegrationSpec {

    def elementService
    def initCatalogueService

    def "creation of new element is logged"() {
        when:
        DataType type = new DataType(name: "DT4CL").save(failOnError: true)
        Change change = Change.findByChangedId(type.id)

        then:
        change
        change.latestVersionId == type.id
        change.type == ChangeType.NEW_ELEMENT_CREATED
        change.author == null
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
        change.author == null
        change.property == null
        change.newValue == null
        change.oldValue == null
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
        change.author == null
        change.property == 'description'
        change.newValue == changed
        change.oldValue == original
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
        type.delete(flush: true)
        Change change = Change.findByChangedIdAndType(type.id, ChangeType.ELEMENT_DELETED)

        then:
        change
        change.latestVersionId == type.id
        change.author == null
        change.property == null
        change.newValue == null
        change.oldValue == null
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
        change.author   == null
        change.property == 'foo'
        change.newValue == 'bar'
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
        change.author   == null
        change.property == 'foo'
        change.oldValue == 'bar'
        change.newValue == 'boo'
    }

    def "deleting metadata is logged"() {
        DataType type = new DataType(name: 'DT4ANM').save(failOnError: true, flush: true)
        ExtensionValue ext = new ExtensionValue(name: 'foo', extensionValue: 'bar', element: type).save(failOnError: true, flush: true)
        ext.delete(failOnError: true, flush: true)
        type.refresh()

        Change change = Change.findByChangedIdAndType(type.id, ChangeType.METADATA_DELETED)

        expect:
        !type.ext.foo

        change
        change.latestVersionId == type.id
        change.author   == null
        change.property == 'foo'
        change.oldValue == 'bar'
        change.newValue == null
    }

    def "adding relationship is logged"() {
        initCatalogueService.initDefaultRelationshipTypes()

        DataType type = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)
        DataType base = new DataType(name: 'DT4ANR').save(failOnError: true, flush: true)

        type.addToIsBasedOn base

        Change change1 = Change.findByChangedIdAndType(type.id, ChangeType.RELATIONSHIP_CREATED)
        Change change2 = Change.findByChangedIdAndType(base.id, ChangeType.RELATIONSHIP_CREATED)


        expect:
        base in type.isBasedOn
        type in base.isBaseFor

        change1
        change1.latestVersionId == type.id
        change1.author   == null
        change1.property == 'is based on'
        change1.newValue == AuditService.storeValue(base)
        change1.oldValue == null

        change2
        change2.latestVersionId == base.id
        change2.author   == null
        change2.property == 'is base for'
        change2.newValue == AuditService.storeValue(base)
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
        change1.author   == null
        change1.property == 'is based on'
        change1.newValue == AuditService.storeValue(base)
        change1.oldValue == null

        change2
        change2.latestVersionId == base.id
        change2.author   == null
        change2.property == 'is base for'
        change2.newValue == AuditService.storeValue(base)
        change2.oldValue == null
    }



}
