package org.modelcatalogue.core.policy;

import com.google.common.collect.ImmutableList;
import org.modelcatalogue.core.CatalogueElement;
import org.modelcatalogue.core.DataModel;

public interface Policy {

    ImmutableList<Convention> getConventions();

    /**
     * Verifies the data model and all declared elements against this policy.
     * @param dataModel the current data model
     */
    void verifyAll(DataModel dataModel);

    /**
     * Verifies single element against this policy.
     *
     * The element doesn't have to be persisted yet nor it doesn't have to have the data model declared yet.
     * @param phase
     * @param dataModel the effective data model
     * @param element the element to be checked by this policy
     */
    void verifySingle(VerificationPhase phase, DataModel dataModel, CatalogueElement element);

}
