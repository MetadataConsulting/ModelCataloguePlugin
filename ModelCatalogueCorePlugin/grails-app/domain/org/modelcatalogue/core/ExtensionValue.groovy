package org.modelcatalogue.core

class ExtensionValue implements Extension {


    def auditService

    String name
    String extensionValue

    static belongsTo = [element: CatalogueElement]

    static constraints = {
        name size: 1..255
        extensionValue maxSize: 2000, nullable: true
    }


    @Override
    public String toString() {
        return "extension for ${element} (${name}=${extensionValue})"
    }

    void afterInsert() {
        auditService.logNewMetadata(this)
    }

    void beforeUpdate() {
        auditService.logMetadataUpdated(this)
    }

    void beforeRemove() {
        auditService.logMetadataDeleted(this)
    }

}
