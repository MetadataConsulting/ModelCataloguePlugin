package org.modelcatalogue.core.util.builder

import org.modelcatalogue.builder.api.ConventionBuilder
import org.modelcatalogue.builder.api.DataModelPolicyBuilder
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.policy.PolicyBuilder

class DefaultDataModelPolicyBuilder implements DataModelPolicyBuilder {

    final PolicyBuilder builder

    DefaultDataModelPolicyBuilder(PolicyBuilder builder) {
        this.builder = builder
    }

    @Override
    void convention(@DelegatesTo(ConventionBuilder.class) Closure builder) {
        new DefaultConventionBuilder(this.builder.check(CatalogueElement)).with builder
    }


}
