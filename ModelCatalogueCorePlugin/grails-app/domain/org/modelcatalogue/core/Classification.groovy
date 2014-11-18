package org.modelcatalogue.core


class Classification extends CatalogueElement {

    /**
     * @deprecated use model catalogue id instead
     */
    String namespace

    static constraints = {
        name unique: 'versionNumber'
        namespace nullable: true, unique: true
    }

    static searchable = {
        name boost:5
        except = ['incomingRelationships', 'outgoingRelationships']
    }

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
