package org.modelcatalogue.core.util.builder

import org.modelcatalogue.builder.api.CatalogueBuilder

class DefaultWithOptionalOrClause implements WithOptionalOrClause {

    final CatalogueBuilder builder

    DefaultWithOptionalOrClause(CatalogueBuilder builder) {
        this.builder = builder
    }

    void or(Closure c) {
        builder.with c
    }

}