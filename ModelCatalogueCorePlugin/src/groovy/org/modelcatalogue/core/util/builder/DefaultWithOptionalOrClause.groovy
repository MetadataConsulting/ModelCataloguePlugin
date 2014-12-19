package org.modelcatalogue.core.util.builder

enum DefaultWithOptionalOrClause implements WithOptionalOrClause {

    INSTANCE

    void or(Closure c) {
        c()
    }

}