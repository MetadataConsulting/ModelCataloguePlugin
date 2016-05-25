package org.modelcatalogue.core.util.builder

import org.modelcatalogue.builder.api.BuilderKeyword
import org.modelcatalogue.builder.api.ConventionBuilder
import org.modelcatalogue.builder.api.DataModelPolicyBuilder
import org.modelcatalogue.builder.api.ModelCatalogueTypes
import org.modelcatalogue.core.policy.PolicyBuilder

class DefaultDataModelPolicyBuilder implements DataModelPolicyBuilder {

    final PolicyBuilder builder

    DefaultDataModelPolicyBuilder(PolicyBuilder builder) {
        this.builder = builder
    }

    @Override
    ConventionBuilder check(BuilderKeyword domain) {
        if (domain instanceof ModelCatalogueTypes) {
            return new DefaultConventionBuilder(this.builder.check(domain.implementation))
        }
        throw new IllegalArgumentException("$domain is not valid input for policy target")
    }

}
