package org.modelcatalogue.core

import org.modelcatalogue.core.publishing.Publisher
import org.modelcatalogue.core.publishing.PublishingChain

class Classification extends CatalogueElement {

    /**
     * @deprecated use model catalogue id instead
     */
    String getNamespace() {
        modelCatalogueId
    }

    /**
     * @deprecated use model catalogue id instead
     */
    void setNamespace(String namespace) {
        modelCatalogueId = namespace
    }

    static constraints = {
        name unique: 'versionNumber'
    }

    static searchable = {
        name boost:5
        except = ['incomingRelationships', 'outgoingRelationships']
    }

    static transients = ['namespace']

    static relationships = [
            outgoing: [classification: 'classifies', classificationFilter: 'usedAsFilterBy']
    ]

    @Override
    CatalogueElement publish(Publisher<CatalogueElement> publisher) {
        PublishingChain.finalize(this)
        .add(this.classifies)
        .run(publisher)
    }
}
