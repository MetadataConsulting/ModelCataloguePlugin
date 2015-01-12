package org.modelcatalogue.core

import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.publishing.Publisher
import org.modelcatalogue.core.publishing.PublishingChain

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
