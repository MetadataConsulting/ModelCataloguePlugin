package org.modelcatalogue.core

class RelationshipMetadata implements Extension {

    String name
    String extensionValue

    static belongsTo = [relationship: Relationship]

    static constraints = {
        name size: 1..255
        extensionValue maxSize: 1000, nullable: true
    }


    @Override
    public String toString() {
        return "metadata for ${relationship} (${name}=${extensionValue})"
    }


}
