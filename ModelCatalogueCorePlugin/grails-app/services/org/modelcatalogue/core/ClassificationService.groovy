package org.modelcatalogue.core

import grails.gorm.DetachedCriteria

class ClassificationService {

    static transactional = false

    def modelCatalogueSecurityService

    public <T> DetachedCriteria<T> classificationAware(DetachedCriteria<T> criteria) {
        // this is not valid classification, just helper
        if (criteria.getPersistentEntity().name == 'User') {
            return criteria
        }

        if (!modelCatalogueSecurityService.isUserLoggedIn()) {
            return criteria
        }
        Set<Classification> classifications = modelCatalogueSecurityService.getCurrentUser().getClassifications()

        if (!classifications) {
            return criteria
        }

        if (criteria.persistentEntity.hasProperty('classification', Classification)) {
            criteria.or {
                criteria.isNull 'classification'
                if (classifications.size() == 1) {
                    criteria.eq 'classification', classifications[0]
                } else {
                    criteria.in 'classification', classifications
                }
            }
        }
        if (criteria.persistentEntity.associations.any{ it.name == 'classifications'}) {
            // TODO: add classifications criteria
        }
        criteria
    }
}
