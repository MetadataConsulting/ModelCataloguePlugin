package org.modelcatalogue.core.policy

import static com.google.common.base.Preconditions.checkNotNull
import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.HibernateHelper

class UniqueChecker implements ConventionChecker {

    @Override
    def <T extends CatalogueElement & GroovyObject> void check(VerificationPhase phase, DataModel model, Class<T> resource, T item, String property, String ignored, String messageOverride, boolean errorsToItem) {
        final Long dataModelId = model.id

        if (!dataModelId) {
            return
        }

        if (property == null) {
            return
        }

        if (!item.hasProperty(property)) {
            return
        }

        if (Conventions.isExtensionAlias(property)) {
            throw new IllegalArgumentException("Not yet implemented for extensions")
        }

        CatalogueElement.withNewSession {
            DataModel dataModel = DataModel.load(dataModelId)

            Object value = item.getProperty(property)

            DetachedCriteria<? extends T> criteria = new DetachedCriteria<? extends T>(resource)
            criteria.eq('dataModel', dataModel).eq(property, value).ne('status', ElementStatus.DEPRECATED)

            if (item.id) {
                if (criteria.list().find { (it as CatalogueElement).getId() != item.getId() }) {
                    (errorsToItem ? item : model).errors.reject('uniqueChecker.duplicate', [HibernateHelper.ensureNoProxy(dataModel), ignored, item, property, ignored] as Object[], messageOverride ?: "Property {3} must be unique for every {2} within {0}")
                }
            } else {
                if (criteria.count()) {
                    (errorsToItem ? item : model).errors.reject('uniqueChecker.duplicate', [HibernateHelper.ensureNoProxy(dataModel), ignored, item, property, ignored] as Object[], messageOverride ?: "Property {3} must be unique for every {2} within {0}")
                }
            }
        }

    }
}
