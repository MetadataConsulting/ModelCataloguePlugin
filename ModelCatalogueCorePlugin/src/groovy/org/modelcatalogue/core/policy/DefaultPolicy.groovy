package org.modelcatalogue.core.policy

import com.google.common.collect.ImmutableList
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel

@CompileStatic @PackageScope class DefaultPolicy implements Policy {

    private final ImmutableList<Convention> conventions;

    DefaultPolicy(ImmutableList<Convention> conventions) {
        this.conventions = conventions
    }

    @Override
    void verify(DataModel model) {
        // we might need something more efficient than DataModel#declares in future
        for (CatalogueElement element in model.declares) {
            for (Convention convention in conventions) {
                if (element.instanceOf(convention.target)) {
                    convention.verify(model, element)
                }
            }
        }
    }
}
