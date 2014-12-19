package org.modelcatalogue.core.util.builder

import org.modelcatalogue.core.CatalogueElement

class CatalogueElementById<T extends CatalogueElement> extends AbstractCatalogueElementProxy<T> {

    final Object id

    CatalogueElementById(CatalogueElementProxyRepository repository, Class<T> domain, String name, Object id) {
        super(repository, domain, name)
        this.id = id
    }

    @Override
    T findExisting() {
        repository.findById(domain, id)
    }

    @Override
    String toString() {
        "Proxy for $domain[name: $name, id: $id]"
    }
}
