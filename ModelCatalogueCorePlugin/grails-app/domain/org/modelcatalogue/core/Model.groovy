package org.modelcatalogue.core

import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.publishing.Publisher
import org.modelcatalogue.core.publishing.PublishingChain

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
    CatalogueElement createDraftVersion(Publisher<CatalogueElement> publisher, DraftContext strategy) {
        PublishingChain.createDraft(this, strategy)
        .add(this.childOf)
        .add(this.classifications)
        .run(publisher)
    }
}
