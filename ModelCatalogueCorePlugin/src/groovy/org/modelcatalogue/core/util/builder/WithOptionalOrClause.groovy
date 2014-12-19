package org.modelcatalogue.core.util.builder

interface WithOptionalOrClause {
    static WithOptionalOrClause NOOP = {}
    void or(@DelegatesTo(CatalogueBuilder) Closure orClosure)
}