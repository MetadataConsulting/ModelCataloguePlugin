package org.modelcatalogue.core.policy

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel

@CompileStatic @PackageScope class DefaultConvention implements Convention {

    final Class<? extends CatalogueElement> target
    final String property
    final ConventionChecker checker
    final String configuration
    final String message

    DefaultConvention(Class<? extends CatalogueElement> target, String property, ConventionChecker checker, String configuration, String message) {
        this.target = target
        this.property = property
        this.checker = checker
        this.configuration = configuration
        this.message = message
    }

    @Override
    void verify(DataModel model, CatalogueElement item) {
        checker.check(model, target, item, property, configuration, message)
    }

    @Override
    String toString() {
        StringBuilder builder = new StringBuilder("check ")
        builder << Conventions.getClassNameOrShortcut(target)
        if (property) {
            if (Conventions.isExtensionAlias(property)) {
                builder << ' extension ' << Conventions.getExtension(property).replace('\'', '\\\'')
            } else {
                builder << ' property \'' << property.replace('\'', '\\\'') << '\''
            }
        }
        if (checker) {
            if (configuration) {
                builder << ' apply ' << Conventions.getCheckerName(checker) << ': \'' << configuration.replace('\'', '\\\'') << '\''
            } else {
                builder << ' is \'' << Conventions.getCheckerName(checker) << '\''
            }
        }
        if (message) {
            builder << ' otherwise \'' << message.replace('\'', '\\\'') << '\''
        }
        return builder.toString()
    }

}
