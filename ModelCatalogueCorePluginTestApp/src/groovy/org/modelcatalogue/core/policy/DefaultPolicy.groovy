package org.modelcatalogue.core.policy

import static org.modelcatalogue.core.policy.VerificationPhase.FINALIZATION_CHECK
import com.google.common.collect.ImmutableList
import com.google.common.collect.Iterables
import groovy.transform.PackageScope
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel

@PackageScope class DefaultPolicy implements Policy {

    final ImmutableList<Convention> conventions;

    DefaultPolicy(ImmutableList<Convention> conventions) {
        this.conventions = conventions
    }

    @Override
    void verifyAll(DataModel model) {
        for (CatalogueElement element in Iterables.concat(model.declares, ImmutableList.of(model))) {
            for (Convention convention in conventions) {
                if (element.instanceOf(convention.target)) {
                    convention.verify(FINALIZATION_CHECK, model, element, false)
                }
            }
        }
    }

    @Override
    void verifySingle(VerificationPhase phase, DataModel model, CatalogueElement element) {
        for (Convention convention in conventions) {
            if (element.instanceOf(convention.target)) {
                convention.verify(phase, model, element, true)
            }
        }
    }

    @Override
    String toString() {
        conventions.collect { it.toString() }.join('\n')
    }
}
