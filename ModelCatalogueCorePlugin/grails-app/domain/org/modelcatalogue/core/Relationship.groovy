package org.modelcatalogue.core

import org.modelcatalogue.core.util.ExtensionsWrapper
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.OrderedMap

/*
* Users can create relationships between all catalogue elements. They include
* DataType, ConceptualDomain, MeasurementUnit, Model, ValueDomain, DataElement
* Relationships have a source element, a destination element and a relationship type.
* There are a number of different predefined relationship types that describe the ways catalogue
* elements are related in the model catalogue

        * ----------------- ------------------ ------------- -----------------------  ----------------------
        | Source           | Relationship     | Destination | Source->Destination    |  Destination<-Source |
        | ---------------- | -----------------| ----------- | ---------------------- | -------------------- |
        | Model            | [containment]    | DataElement |  "contains"            |  "contained in"      |
        | DataElement      | [instantiation]  | ValueDomain | "instantiated by"      | "instantiates"       |
        | Model            | [heirachical]    | Model       | "parentOf"             | "ChildOf"            |
        | DataElement      | [supersession]   | DataElement | "supercedes"           | "supercededBy"       |

*
* New types can be created using the ontology type class
*
*/

class Relationship implements Extendible<RelationshipMetadata> {

    CatalogueElement source
    CatalogueElement destination

    RelationshipType relationshipType

    Classification classification

    Long outgoingIndex = System.currentTimeMillis()
    Long incomingIndex = System.currentTimeMillis()

    /*
     * Reordeing bidirectional relationships is not supported as the combined index is
     * actually same for all group of related elements
     * and change from the other side would change the view from the opposite side
     */
    @Deprecated
    Long combinedIndex = System.currentTimeMillis()

    static hasMany = [extensions: RelationshipMetadata]
    static transients = ['ext']

    Boolean archived = false

    final Map<String, String> ext = new ExtensionsWrapper(this)

    void setExt(Map<String, String> ext) {
        for (String key in this.ext.keySet() - ext.keySet()) {
            this.ext.remove key
        }
        this.ext.putAll(OrderedMap.fromJsonMap(ext))
    }

    static belongsTo = [source: CatalogueElement, destination: CatalogueElement]

    static constraints = {
        classification nullable: true
    }

    static mapping = {
        extensions lazy: false, sort: 'orderIndex'
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, source: ${source}, destination: ${destination}, type: ${relationshipType?.name}]"
    }

    def beforeDelete(){
        if (source) {
            source?.removeFromOutgoingRelationships(this)
        }
        if(destination){
            destination?.removeFromIncomingRelationships(this)
        }
    }

    void resetIndexes() {
        outgoingIndex = System.currentTimeMillis()
        incomingIndex = System.currentTimeMillis()
        combinedIndex = System.currentTimeMillis()
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
            return newOne
        }
        throw new IllegalStateException("Cannot add extension before saving the element (id: ${getId()}, attached: ${isAttached()})")
    }

    @Override
    void removeExtension(RelationshipMetadata extension) {
        removeFromExtensions(extension).save()
        extension.delete(flush: true)
    }

    @Override
    RelationshipMetadata updateExtension(RelationshipMetadata old, String value) {
        if (old.extensionValue == value) {
            old.orderIndex = System.currentTimeMillis()
            FriendlyErrors.failFriendlySaveWithoutFlush(old)
            return old
        }
        old.extensionValue = value
        FriendlyErrors.failFriendlySaveWithoutFlush(old)
    }
}
