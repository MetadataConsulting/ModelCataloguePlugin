package org.modelcatalogue.core.policy

import static com.google.common.base.Preconditions.checkNotNull
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import java.util.regex.Pattern

class RegexChecker implements ConventionChecker {

    @Override
    def <T extends CatalogueElement & GroovyObject> void check(VerificationPhase phase, DataModel model, Class<T> ignored, T item, String property, String configuration, String messageOverride, boolean errorsToItem) {
        checkNotNull(property, 'Property must be set', new Object[0])
        checkNotNull(configuration, 'Regex must be set', new Object[0])

        if (phase == VerificationPhase.PROPERTY_CHECK && Conventions.isExtensionAlias(property)) {
            return
        }

        Pattern pattern = Pattern.compile(configuration)

        String value = Conventions.getValueOrName(Conventions.getPropertyOrExtension(item, property))
        if (value && !pattern.matcher(value).matches()) {
            (errorsToItem ? item : model).errors.reject('regexChecker.no.match', [model, ignored, item, property, configuration] as Object[], messageOverride ?: "Property {3} of {2} does not match /{4}/")
        }
    }
}
