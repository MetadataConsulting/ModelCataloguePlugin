package org.modelcatalogue.core

import org.modelcatalogue.core.publishing.Publisher
import org.modelcatalogue.core.publishing.PublishingChain
import org.modelcatalogue.core.util.Legacy

class Model extends CatalogueElement {

    static relationships = [
            incoming: [hierarchy: 'childOf'],
            outgoing: [containment: 'contains', hierarchy: 'parentOf']
    ]

    @Override
    CatalogueElement publish(Publisher<CatalogueElement> publisher) {
        PublishingChain.finalize(this)
        .add(this.contains)
        .add(this.parentOf)
        .run(publisher)
    }

    @Override
    protected PublishingChain prepareDraftChain(PublishingChain chain) {
        chain.add(this.childOf).add(this.classifications)
    }

    @Override
    String getModelCatalogueId() {
        // TODO: remove when the class is renamed
        return Legacy.fixModelCatalogueId(super.getModelCatalogueId())
    }
}
