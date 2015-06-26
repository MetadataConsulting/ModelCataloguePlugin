package org.modelcatalogue.builder.util

import org.modelcatalogue.builder.api.CatalogueBuilder

abstract class CatalogueBuilderScript extends Script {

    @Delegate(deprecated = true) CatalogueBuilder delegate

    abstract configure()

    @Override Object run() {
        delegate = binding.builder
        delegate.build {
            configure()
        }
    }

}
