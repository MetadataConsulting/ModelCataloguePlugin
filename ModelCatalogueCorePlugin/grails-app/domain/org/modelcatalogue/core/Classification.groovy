package org.modelcatalogue.core

import org.modelcatalogue.core.publishing.Publisher
import org.modelcatalogue.core.publishing.PublishingChain
import org.modelcatalogue.core.util.Legacy

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

    @Override
    String getModelCatalogueId() {
        // TODO: remove when the class is renamed
        return Legacy.fixModelCatalogueId(super.getModelCatalogueId())
    }
}
