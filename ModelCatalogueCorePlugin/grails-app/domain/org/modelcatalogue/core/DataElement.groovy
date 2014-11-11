package org.modelcatalogue.core
/*
* A data element is an atomic unit of data
* i.e. xml  <xs:element name="title" />
*
* */

class DataElement extends CatalogueElement {

    ValueDomain valueDomain

    static constraints = {
        valueDomain nullable: true
    }

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
        modelCatalogueId boost:10
        name boost:5
        extensions component:true
        except = ['incomingRelationships', 'outgoingRelationships', 'valueDomain']
    }

    static relationships = [
            incoming: [containment: 'containedIn'],
    ]

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, version: ${version}, status: ${status}, modelCatalogueId: ${modelCatalogueId}]"
    }

    @Override
    CatalogueElement publish(Archiver<CatalogueElement> archiver) {
        PublishingChain
                .create(this)
                .publish(this.valueDomain)
                .publish(archiver)
    }

}
