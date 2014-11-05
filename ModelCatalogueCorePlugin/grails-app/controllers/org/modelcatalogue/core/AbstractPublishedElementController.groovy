package org.modelcatalogue.core

class AbstractPublishedElementController<T extends PublishedElement> extends AbstractCatalogueElementController<T> {

    AbstractPublishedElementController(Class<T> type, boolean readOnly) {
        super(type, readOnly)
    }

}
