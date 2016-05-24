package org.modelcatalogue.core.util.builder

import org.modelcatalogue.builder.api.BuilderKeyword
import org.modelcatalogue.builder.api.ConventionBuilder
import org.modelcatalogue.builder.api.ModelCatalogueTypes
import org.modelcatalogue.core.policy.ConventionBuilder as PolicyConventionBuilder

class DefaultConventionBuilder implements ConventionBuilder {

    private final PolicyConventionBuilder builder

    DefaultConventionBuilder(org.modelcatalogue.core.policy.ConventionBuilder builder) {
        this.builder = builder
    }

    @Override
    ConventionBuilder target(BuilderKeyword domain) {
        if (domain instanceof ModelCatalogueTypes) {
            builder.check(domain.implementation)
        } else {
            throw new IllegalArgumentException("$domain is not valid input for policy target")
        }
        return this
    }

    @Override
    ConventionBuilder type(String type) {
        builder.is(type)
        return this
    }

    @Override
    ConventionBuilder argument(String argument) {
        builder.configuration(argument)
        return this
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
    ConventionBuilder message(String message) {
        builder.otherwise(message)
        return this
    }


}
