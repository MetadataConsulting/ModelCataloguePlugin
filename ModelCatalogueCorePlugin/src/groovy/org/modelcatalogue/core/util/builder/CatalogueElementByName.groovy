package org.modelcatalogue.core.util.builder

import org.modelcatalogue.core.CatalogueElement

class CatalogueElementByName<T extends CatalogueElement> extends AbstractCatalogueElementProxy<T> {

    CatalogueElementByName(CatalogueElementProxyRepository repository, Class<T> domain, String name) {
        super(repository, domain, name)
    }

    @Override
    T findExisting() {
        repository.tryFindUnclassified(domain, name, null)
    }

    @Override
    String toString() {
        "Proxy for $domain[name: $name]"
    }
}
