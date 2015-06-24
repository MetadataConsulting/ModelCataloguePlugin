package org.modelcatalogue.core.util.builder

import org.modelcatalogue.api.builder.CatalogueBuilder
import org.modelcatalogue.api.builder.WithOptionalOrClause

class DefaultWithOptionalOrClause implements WithOptionalOrClause {

    final CatalogueBuilder builder

    DefaultWithOptionalOrClause(CatalogueBuilder builder) {
        this.builder = builder
    }

    void or(Closure c) {
        builder.with c
    }

}