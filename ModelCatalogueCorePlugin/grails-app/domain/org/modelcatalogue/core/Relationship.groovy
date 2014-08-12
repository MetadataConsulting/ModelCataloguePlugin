package org.modelcatalogue.core

import org.modelcatalogue.core.util.ExtensionsWrapper

/*
* Users can create relationships between all catalogue elements. They include
* DataType, ConceptualDomain, MeasurementUnit, Model, ValueDomain, DataElement
* Relationshipss have a source element, a destination element and a relationship type.
* There are a number of different predefined relationship types that describe the ways catalogue
* elements are related in the model catalogue

        * ----------------- ------------------ ------------- -----------------------  ----------------------
        | Source           | Relationship     | Destination | Source->Destination    |  Destination<-Source |
        | ---------------- | -----------------| ----------- | ---------------------- | -------------------- |
        | ConceptualDomain |  [context]       |  Model      | "provides context for" | "has context of"     |
        | ConceptualDomain | [inclusion]      | ValueDomain |  "includes"            | "included in"        |
        | Model            | [containment]    | DataElement |  "contains"            |  "contained in"      |
        | DataElement      | [instantiation]  | ValueDomain | "instantiated by"      | "instantiates"       |
        | Model            | [heirachical]    | Model       | "parentOf"             | "ChildOf"            |
        | DataElement      | [supersession]   | DataElement | "supercedes"           | "supercededBy"       |

*
* New types can be created using the ontology type class
*
*/

class Relationship implements Extendible {

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
        extensions component:true
        relationshipType component:true
        source component: true
        destination component: true
    }

    CatalogueElement source
    CatalogueElement destination

    RelationshipType relationshipType

    static hasMany = [extensions: RelationshipMetadata]
    static transients = ['ext']

    final Map<String, String> ext = new ExtensionsWrapper(this)

    void setExt(Map<String, String> ext) {
        this.ext.clear()
        this.ext.putAll(ext)
    }

    Boolean archived = false

    static mapping = {
        extensions lazy: false
    }

    static belongsTo = [source: CatalogueElement, destination: CatalogueElement]

    static constraints = {
        relationshipType unique: ['source', 'destination'], validator: { val, obj ->

            if (!val) return true;

            String errorMessage = val.validateSourceDestination(obj.source, obj.destination, obj.ext)
            if (errorMessage) {
                return errorMessage;
            }
            return true;

        }
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

    @Override
    Set<Extension> listExtensions() {
        extensions
    }

    @Override
    Extension addExtension(String name, String value) {
        RelationshipMetadata newOne = new RelationshipMetadata(name: name, extensionValue: value, relationship: this)
        newOne.save()
        assert !newOne.errors.hasErrors()
        addToExtensions(newOne)
        newOne
    }

    @Override
    void removeExtension(Extension extension) {
        if (extension instanceof RelationshipMetadata) {
            removeFromExtensions(extension)
            extension.delete(flush: true)
        } else {
            throw new IllegalArgumentException("Only instances of RelationshipMetadata are supported")
        }
    }

}
