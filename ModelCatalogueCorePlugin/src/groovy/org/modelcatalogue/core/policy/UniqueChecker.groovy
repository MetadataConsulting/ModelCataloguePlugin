package org.modelcatalogue.core.policy

import grails.gorm.DetachedCriteria
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel

import static com.google.common.base.Preconditions.checkNotNull

@CompileStatic class UniqueChecker implements ConventionChecker {

    @Override
    def <T extends CatalogueElement & GroovyObject> void check(VerificationPhase phase, DataModel model, Class<T> resource, T item, String property, String ignored, String messageOverride, boolean errorsToItem) {
        checkNotNull(property, 'Property must be set', new Object[0])

        if (!item.readyForQueries) {
            return
        }

        if (!item.hasProperty(property)) {
            return
        }

        if (Conventions.isExtensionAlias(property)) {
            throw new IllegalArgumentException("Not yet implemented for extensions")
        }

        Object value = item.getProperty(property)

        DetachedCriteria<? extends T> criteria = new DetachedCriteria<? extends T>(resource)
        criteria.eq('dataModel', model).eq(property, value)

        if (criteria.count() > 1) {
            (errorsToItem ? item : model).errors.reject('uniqueChecker.duplicate', [model, ignored, item, property, ignored] as Object[], messageOverride ?: "Property {3} must be unique for every {2} within {0}")
        }
    }
}
