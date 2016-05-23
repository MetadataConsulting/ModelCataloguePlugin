package org.modelcatalogue.core.policy

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel

import static com.google.common.base.Preconditions.checkNotNull

class RequiredChecker implements ConventionChecker {

    @Override
    def <T extends CatalogueElement> void check(DataModel model, Class<T> ignoredResource, T item, String property, String ignored, String messageOverride) {
        checkNotNull(property, 'Property must be set', new Object[0])

        Object value = ConventionCheckers.getPropertyOrExtension(item, property)
        if (!value) {
            model.errors.reject('requiredChecker.missing', messageOverride ?:  "Property $property of $item is required")
        }
    }
}
