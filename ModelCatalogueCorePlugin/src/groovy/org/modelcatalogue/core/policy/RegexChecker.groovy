package org.modelcatalogue.core.policy

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel

import java.util.regex.Pattern

import static com.google.common.base.Preconditions.checkNotNull

@CompileStatic class RegexChecker implements ConventionChecker {

    @Override
    def <T extends CatalogueElement & GroovyObject> void check(DataModel model, Class<T> ignored, T item, String property, String configuration, String messageOverride) {
        checkNotNull(property, 'Property must be set', new Object[0])
        checkNotNull(configuration, 'Regex must be set', new Object[0])

        Pattern pattern = Pattern.compile(configuration)

        String value = Conventions.getValueOrName(Conventions.getPropertyOrExtension(item, property))
        if (value && !pattern.matcher(value).matches()) {
            model.errors.reject('regexChecker.no.match', [model, ignored, item, property, configuration] as Object[], messageOverride ?: "Property {3} of {2} does not match /{4}/")
        }
    }
}
