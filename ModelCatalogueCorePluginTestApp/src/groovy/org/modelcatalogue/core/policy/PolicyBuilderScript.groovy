package org.modelcatalogue.core.policy

import org.modelcatalogue.core.CatalogueElement

abstract class PolicyBuilderScript extends Script implements KnownCheckersShortcuts, DomainClassesShortcuts {

    private PolicyBuilder builder = new PolicyBuilder()

    @Override final Policy run() {
        build()
        builder.createPolicy()
    }

    abstract void build();
    public <T extends CatalogueElement & GroovyObject> ConventionBuilder check(Class<T> target) { builder.check(target) }
}
