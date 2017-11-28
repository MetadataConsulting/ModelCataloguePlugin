package org.modelcatalogue.core.policy

import static com.google.common.base.Preconditions.checkNotNull
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel

class RequiredChecker implements ConventionChecker {

    @Override
    def <T extends CatalogueElement & GroovyObject> void check(VerificationPhase phase, DataModel model, Class<T> ignoredResource, T item, String property, String ignored, String messageOverride, boolean errorsToItem) {
        checkNotNull(property, 'Property must be set', new Object[0])

        if (phase == VerificationPhase.PROPERTY_CHECK && Conventions.isExtensionAlias(property)) {
            return
        }

        Object value = Conventions.getPropertyOrExtension(item, property)
        if (!value) {
            (errorsToItem ? item : model).errors.reject('requiredChecker.missing',  [model, ignored, item, property, ignored] as Object[], messageOverride ?:  "Property {3} of {2} is required")
        }
    }
}
