package org.modelcatalogue.core.policy;

import groovy.lang.GroovyObject;
import org.modelcatalogue.core.CatalogueElement;
import org.modelcatalogue.core.DataModel;

/**
 * Performs the check against the given data model and rejects values of this data model if any problem encounters.
 */
public interface ConventionChecker {

    /**
     * Performs the check against the given data model and rejects values of this data model if any problem encounters.
     * @param model data model to be checked
     * @param resource type of the class for which the checker applies
     * @param target target class to be checked
     * @param property target property to be checked
     * @param configuration additional configuration
     * @param messageOverride error message to override the default one, you can interpolate following messages
     *                        0: data model
     *                        1: target entity class
     *                        2: target entity instance
     *                        3: property
     *                        4: configuration
     */
    <T extends CatalogueElement & GroovyObject> void check(DataModel model, Class<T> resource, T target, String property, String configuration, String messageOverride);

}
