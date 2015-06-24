package org.modelcatalogue.core.util.builder

import org.modelcatalogue.core.api.builder.CatalogueBuilder

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
