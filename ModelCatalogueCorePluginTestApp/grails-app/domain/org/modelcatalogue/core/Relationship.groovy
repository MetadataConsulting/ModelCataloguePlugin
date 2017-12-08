package org.modelcatalogue.core

import org.modelcatalogue.core.util.*

/*
* Users can create relationships between all catalogue elements. They include
* DataType, ConceptualDomain, MeasurementUnit, Model, DataElement
* Relationships have a source element, a destination element and a relationship type.
* There are a number of different predefined relationship types that describe the ways catalogue
* elements are related in the model catalogue

        * ----------------- ------------------ ------------- -----------------------  ----------------------
        | Source           | Relationship     | Destination | Source->Destination    |  Destination<-Source |
        | ---------------- | -----------------| ----------- | ---------------------- | -------------------- |
        | Model            | [containment]    | DataElement |  "contains"            |  "contained in"      |
        | Model            | [heirachical]    | Model       | "parentOf"             | "ChildOf"            |
        | DataElement      | [supersession]   | DataElement | "supercedes"           | "supercededBy"       |

*
* New types can be created using the ontology type class
*
*/
class Relationship implements Extendible<RelationshipMetadata>, org.modelcatalogue.core.api.Relationship, DataModelAware {

    // when the relationship class is first loaded set the next index to current time in milliseconds
    static long nextIndex = System.currentTimeMillis()

    static long getNextIndex() {
        ++nextIndex;
    }

    def auditService
    def relationshipService

    CatalogueElement source
    CatalogueElement destination

    RelationshipType relationshipType

    DataModel dataModel

    Long outgoingIndex
    Long incomingIndex

    // init the indexes
    {
        resetIndexes()
    }

    static hasMany = [extensions: RelationshipMetadata]
    static transients = ['ext']

    Boolean archived = false
    Boolean inherited = false

    final Map<String, String> ext = new ExtensionsWrapper(this)

    void setExt(Map<String, String> ext) {
        for (String key in this.ext.keySet() - ext.keySet()) {
            this.ext.remove key
        }
        this.ext.putAll(OrderedMap.fromJsonMap(ext))
    }

    static belongsTo = [source: CatalogueElement, destination: CatalogueElement]

    static constraints = {
        dataModel nullable: true
    }

    static mapping = {
        extensions lazy: false, sort: 'orderIndex', cascade: "all-delete-orphan"
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, source: ${source}, destination: ${destination}, type: ${relationshipType?.name}]"
    }

    /**
     * Removes relationship from {@link #source} and {@link #destination}. This method causes {@link Relationship} object
     * to be in inconsistent state and should be use before its delete.
     */
    def clearRelationships() {
        if (source) {
            source.removeFromOutgoingRelationships(this)
        }
        if (destination) {
            destination.removeFromIncomingRelationships(this)
        }
    }

    void resetIndexes() {
        long nextIndex = getNextIndex()
        outgoingIndex = nextIndex
        incomingIndex = nextIndex
    }

    @Override
    Set<RelationshipMetadata> listExtensions() {
        extensions
    }

    @Override
    RelationshipMetadata findExtensionByName(String name) {
        listExtensions()?.find { it.name == name }
    }

    @Override
    int countExtensions() {
        listExtensions()?.size() ?: 0
    }

    @Override
    RelationshipMetadata addExtension(String name, String value) {
        if (getId() && isAttached()) {
            RelationshipMetadata newOne = new RelationshipMetadata(name: name, extensionValue: value, relationship: this)
            FriendlyErrors.failFriendlySaveWithoutFlush(newOne)
            addToExtensions(newOne).save(validate: false)
            auditService.logNewRelationshipMetadata(newOne)
            Inheritance.withChildren(source) {
                RelationshipDefinition definition = RelationshipDefinition.from(this)
                definition.source = it
                Relationship rel = relationshipService.findExistingRelationship(definition)
                if (rel) {
                    rel.addExtension(name, value)
                }
            }
            return newOne
        }
        throw new IllegalStateException("Cannot add extension before saving the element (id: ${getId()}, attached: ${isAttached()})")
    }

    @Override
    void removeExtension(RelationshipMetadata extension) {
        Inheritance.withChildren(source) {
            RelationshipDefinition definition = RelationshipDefinition.from(this)
            definition.source = it
            Relationship rel = relationshipService.findExistingRelationship(definition)
            if (rel) {
                RelationshipMetadata existing = rel.findExtensionByName(extension.name)
                if (existing.extensionValue == extension.extensionValue) {
                    rel.removeExtension(existing)
                }
            }
        }
        auditService.logRelationshipMetadataDeleted(extension)
        removeFromExtensions(extension).save()
        extension.delete(flush: true)
    }

    @Override
    RelationshipMetadata updateExtension(RelationshipMetadata old, String value) {
        Inheritance.withChildren(source) {
            RelationshipDefinition definition = RelationshipDefinition.from(this)
            definition.source = it
            Relationship rel = relationshipService.findExistingRelationship(definition)
            if (rel) {
                RelationshipMetadata existing = rel.findExtensionByName(old.name)
                if (existing.extensionValue == old.extensionValue) {
                    rel.updateExtension(existing, value)
                }
            }
        }
        if (old.extensionValue == value) {
            old.orderIndex = System.currentTimeMillis()
            FriendlyErrors.failFriendlySaveWithoutFlush(old)
            return old
        }
        old.extensionValue = value
        if (old.validate()) {
            auditService.logRelationshipMetadataUpdated(old)
        }
        FriendlyErrors.failFriendlySaveWithoutFlush(old)
    }


}
