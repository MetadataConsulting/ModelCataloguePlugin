package org.modelcatalogue.core

class Model extends CatalogueElement {

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
        modelCatalogueId boost:10
        name boost:5
        extensions component:true
        except = ['incomingRelationships', 'outgoingRelationships']
    }

    static relationships = [
            incoming: [hierarchy: 'childOf'],
            outgoing: [containment: 'contains', hierarchy: 'parentOf']
    ]

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, version: ${version}, status: ${status}, modelCatalogueId: ${modelCatalogueId}]"
    }

    @Override
    CatalogueElement publish(Archiver<CatalogueElement> archiver) {
        PublishingChain
                .create(this)
                .publish(this.contains)
                .publish(this.parentOf)
                .publish(archiver)
    }

}
