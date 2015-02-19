package org.modelcatalogue.core

class RelationshipMetadata implements Extension {

    /* the name property from catalogue element is a key for the extension */

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    def auditService

    static searchable = {
        name boost:5
        except = ['relationship', 'incomingRelationships', 'outgoingRelationships']
    }

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

    void afterInsert() {
        auditService.logNewRelationshipMetadata(this)
    }

    void beforeUpdate() {
        auditService.logRelationshipMetadataUpdated(this)
    }

    void beforeRemove() {
        auditService.logRelationshipMetadataDeleted(this)
    }

}
