package org.modelcatalogue.core

class Asset extends ExtendibleElement {

    Long    size
    String  contentType
    String  originalFileName
    String  downloadUrl


    static searchable = {
        modelCatalogueId boost:10
        name boost:5
        extensions component:true
        incomingRelationships component: true
        outgoingRelationships component: true
        except = ['ext', 'contains', 'hasContextOf', 'parentOf', 'childOf']
    }

    static constraints = {
        contentType maxSize: 255, nullable: true
        originalFileName maxSize: 255, nullable: true
        downloadUrl maxSize: 2000, nullable: true
        size nullable: true
    }

    static relationships = [
            incoming: [attachment: 'isAttachedTo']
    ]

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, version: ${version}, status: ${status}, modelCatalogueId: ${modelCatalogueId}]"
    }
}
