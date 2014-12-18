package org.modelcatalogue.core


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
            outgoing: [classification: 'classifies']
    ]

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

    @Override
    protected void beforeDraftPersisted() {
        namespace = null
    }

    @Override
    CatalogueElement publish(Archiver<CatalogueElement> archiver) {
        PublishingChain
                .create(this)
                .publish(this.classifies)
                .publish(archiver)
    }
}
