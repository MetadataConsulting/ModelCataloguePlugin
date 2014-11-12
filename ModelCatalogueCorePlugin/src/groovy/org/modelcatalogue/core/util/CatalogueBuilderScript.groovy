package org.modelcatalogue.core.util

abstract class CatalogueBuilderScript extends Script {

    @Delegate CatalogueBuilder delegate

    abstract configure()

    @Override Object run() {
        delegate = binding.builder
        configure()
    }
}
