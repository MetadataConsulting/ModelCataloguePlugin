package org.modelcatalogue.core.util.builder

import groovy.transform.CompileStatic
import org.modelcatalogue.builder.api.CatalogueBuilder

@CompileStatic class DefaultWithOptionalOrClause implements WithOptionalOrClause {

    final CatalogueBuilder builder

    DefaultWithOptionalOrClause(CatalogueBuilder builder) {
        this.builder = builder
    }

    void or(Closure c) {
        builder.with c
    }

}
