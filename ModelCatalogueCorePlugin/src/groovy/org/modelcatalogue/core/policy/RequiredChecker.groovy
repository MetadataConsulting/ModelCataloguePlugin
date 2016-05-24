package org.modelcatalogue.core.policy

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel

import static com.google.common.base.Preconditions.checkNotNull

@CompileStatic class RequiredChecker implements ConventionChecker {

    @Override
    def <T extends CatalogueElement & GroovyObject> void check(DataModel model, Class<T> ignoredResource, T item, String property, String ignored, String messageOverride) {
        checkNotNull(property, 'Property must be set', new Object[0])

        Object value = Conventions.getPropertyOrExtension(item, property)
        if (!value) {
            model.errors.reject('requiredChecker.missing',  [model, ignored, item, property, ignored] as Object[], messageOverride ?:  "Property {3} of {2} is required")
        }
    }
}
