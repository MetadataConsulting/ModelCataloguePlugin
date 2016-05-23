package org.modelcatalogue.core.policy

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel

import java.util.regex.Pattern

import static com.google.common.base.Preconditions.checkNotNull

class RegexChecker implements ConventionChecker {

    @Override
    def <T extends CatalogueElement> void check(DataModel model, T item, String property, String configuration) {
        checkNotNull(property, 'Property must be set', new Object[0])
        checkNotNull(configuration, 'Regex must be set', new Object[0])

        Pattern pattern = Pattern.compile(configuration)

        String value = item.getProperty(property)?.toString()
        if (value && !pattern.matcher(value).matches()) {
            model.errors.reject('regexChecker.no.match', "Property $property of $item does not match /$configuration/")
        }
    }
}
