package org.modelcatalogue.core.policy

import grails.compiler.GrailsCompileStatic
import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.api.ElementStatus

import static com.google.common.base.Preconditions.checkNotNull

@GrailsCompileStatic class UniqueChecker implements ConventionChecker {

    @Override
    def <T extends CatalogueElement & GroovyObject> void check(VerificationPhase phase, DataModel model, Class<T> resource, T item, String property, String ignored, String messageOverride, boolean errorsToItem) {
        if (!model.readyForQueries) {
            return
        }

        checkNotNull(property, 'Property must be set', new Object[0])

        if (!item.hasProperty(property)) {
            return
        }

        if (Conventions.isExtensionAlias(property)) {
            throw new IllegalArgumentException("Not yet implemented for extensions")
        }

        CatalogueElement.withNewSession {
            Object value = item.getProperty(property)

            DetachedCriteria<? extends T> criteria = new DetachedCriteria<? extends T>(resource)
            criteria.eq('dataModel', model).eq(property, value).ne('status', ElementStatus.DEPRECATED)

            if (item.readyForQueries) {
                if (criteria.list().find { (it as CatalogueElement).getId() != item.getId() }) {
                    (errorsToItem ? item : model).errors.reject('uniqueChecker.duplicate', [model, ignored, item, property, ignored] as Object[], messageOverride ?: "Property {3} must be unique for every {2} within {0}")
                }
            } else {
                if (criteria.list()) {
                    (errorsToItem ? item : model).errors.reject('uniqueChecker.duplicate', [model, ignored, item, property, ignored] as Object[], messageOverride ?: "Property {3} must be unique for every {2} within {0}")
                }}

        }

    }
}
