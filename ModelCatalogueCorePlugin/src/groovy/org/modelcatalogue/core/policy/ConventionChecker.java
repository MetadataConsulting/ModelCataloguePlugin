package org.modelcatalogue.core.policy;

import org.modelcatalogue.core.CatalogueElement;
import org.modelcatalogue.core.DataModel;

/**
 * Performs the check against the given data model and rejects values of this data model if any problem encounters.
 */
public interface ConventionChecker {

    /**
     * Performs the check against the given data model and rejects values of this data model if any problem encounters.
     *
     * @param model data model to be checked
     * @param target target class to be checked
     * @param property target property to be checked
     * @param configuration additional configuration
     */
    <T extends CatalogueElement> void check(DataModel model, T target, String property, String configuration);

}
