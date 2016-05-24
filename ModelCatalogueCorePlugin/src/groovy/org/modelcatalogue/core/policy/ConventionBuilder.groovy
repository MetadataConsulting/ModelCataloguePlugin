package org.modelcatalogue.core.policy

import com.google.common.base.Preconditions
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.modelcatalogue.core.CatalogueElement

@CompileStatic class ConventionBuilder {

    private Class<? extends CatalogueElement> target
    private String property
    private ConventionChecker checker
    private String configuration
    private String message

    @PackageScope ConventionBuilder(Class<? extends CatalogueElement> target) {
        this.target = target
    }

    ConventionBuilder property(String property) {
        this.property = property
        return this
    }

    ConventionBuilder extension(String extension) {
        this.property = Conventions.getExtensionAlias(extension)
        return this
    }

    ConventionBuilder configuration(String configuration) {
        this.configuration = configuration
        return this
    }

    ConventionBuilder is(String checkerName) {
        this.checker = Conventions.checkers[checkerName]
        Preconditions.checkNotNull(this.checker, "Checker $checkerName is not registered", new Object[0])
        return this
    }

    ConventionBuilder apply(Map<String, Object> checkerToConf) {
        if (checkerToConf.size() != 1) {
            throw new IllegalArgumentException("Only single parameter expected")
        }
        Map.Entry<String, Object> definition = checkerToConf.entrySet().first()
        this.checker = Conventions.checkers[definition.key]
        this.configuration = definition.value?.toString()
        Preconditions.checkNotNull(this.checker, "Checker $definition.key is not registered", new Object[0])
        return this
    }

    ConventionBuilder otherwise(String errorMessage) {
        this.message = errorMessage
        return this
    }

    ConventionBuilder check(Class<? extends CatalogueElement> target) {
        this.target = target
        return this
    }

    Convention build() {
        return new DefaultConvention(target, property, checker, configuration, message)
    }

}
