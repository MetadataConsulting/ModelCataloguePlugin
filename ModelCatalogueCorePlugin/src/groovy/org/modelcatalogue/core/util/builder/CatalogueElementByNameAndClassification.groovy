package org.modelcatalogue.core.util.builder

import org.modelcatalogue.core.CatalogueElement

class CatalogueElementByNameAndClassification<T extends CatalogueElement> extends AbstractCatalogueElementProxy<T> {

    final String classificationName

    CatalogueElementByNameAndClassification(CatalogueElementProxyRepository repository, Class<T> domain, String classificationName, String name) {
        super(repository, domain, name)
        this.classificationName = classificationName
    }

    @Override
    T findExisting() {
        repository.tryFind(domain, classificationName, name, null)
    }

    @Override
    String toString() {
        "Proxy of $domain[name: $name, classification: $classificationName]"
    }
}
