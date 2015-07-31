package org.modelcatalogue.core

import org.modelcatalogue.core.publishing.Publisher
import org.modelcatalogue.core.publishing.PublishingChain

class ReferenceType extends DataType {

    DataClass dataClass

    static constraints = {
        dataClass nullable: true, fetch: 'join'
    }


    protected PublishingChain prepareDraftChain(PublishingChain chain) {
        super.prepareDraftChain(chain).add(dataClass)
    }

    @Override
    CatalogueElement publish(Publisher<CatalogueElement> publisher) {
        PublishingChain.finalize(this)
                .add(this.dataClass)
                .run(publisher)
    }

}
