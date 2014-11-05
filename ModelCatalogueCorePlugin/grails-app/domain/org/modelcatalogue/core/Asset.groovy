package org.modelcatalogue.core

class Asset extends CatalogueElement {

    Long    size
    String  contentType
    String  originalFileName
    String  md5


    static searchable = {
        modelCatalogueId boost:10
        name boost:5
        extensions component:true
        except = ['md5', 'incomingRelationships', 'outgoingRelationships']
    }

    static constraints = {
        contentType maxSize: 255, nullable: true
        md5 maxSize: 32, nullable: true
        originalFileName maxSize: 255, nullable: true
        size nullable: true
    }

    static relationships = [
            incoming: [attachment: 'isAttachedTo']
    ]


    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, version: ${version}, status: ${status}, modelCatalogueId: ${modelCatalogueId}]"
    }
}
