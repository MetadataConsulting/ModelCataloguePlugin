package org.modelcatalogue.core.util.builder

import org.modelcatalogue.builder.api.ConventionBuilder
import org.modelcatalogue.core.policy.ConventionBuilder as PolicyConventionBuilder

class DefaultConventionBuilder implements ConventionBuilder {

    private final PolicyConventionBuilder builder

    DefaultConventionBuilder(org.modelcatalogue.core.policy.ConventionBuilder builder) {
        this.builder = builder
    }

    @Override
    ConventionBuilder property(String property) {
        builder.property(property)
        return this
    }

    @Override
    ConventionBuilder extension(String extension) {
        builder.extension(extension)
        return this
    }

    @Override
    ConventionBuilder apply(Map<String, Object> checkerToConf) {
        builder.apply(checkerToConf)
        return this
    }

    @Override
    ConventionBuilder is(String argument) {
        builder.is(argument)
        return this
    }

    @Override
    ConventionBuilder otherwise(String message) {
        builder.otherwise(message)
        return this
    }
}
