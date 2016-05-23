package org.modelcatalogue.core.policy

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel

import static com.google.common.base.Preconditions.checkNotNull

class UniqueChecker implements ConventionChecker {

    @Override
    def <T extends CatalogueElement> void check(DataModel model, Class<T> resource, T item, String property, String ignored, String messageOverride) {
        checkNotNull(property, 'Property must be set', new Object[0])

        if (!item.hasProperty(property)) {
            return
        }

        Object value = item.getProperty(property)

        DetachedCriteria<? extends T> criteria = new DetachedCriteria<? extends T>(resource).build {
            eq('dataModel', model)
            eq(property, value)
        }

        if (criteria.count() > 1) {
            model.errors.reject('requiredChecker.missing', messageOverride ?: "Property $property must be unique for every $resource within $model")
        }
    }
}
