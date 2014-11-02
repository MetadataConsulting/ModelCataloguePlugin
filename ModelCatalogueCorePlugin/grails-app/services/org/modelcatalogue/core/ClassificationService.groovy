package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.util.DetachedListWithTotalAndType
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.ListWithTotalAndTypeWrapper
import org.modelcatalogue.core.util.ListWrapper

class ClassificationService {

    static transactional = false

    def modelCatalogueSecurityService

    public <T> ListWrapper<T> classified(ListWrapper<T> list) {
        if (!(list instanceof ListWithTotalAndTypeWrapper)) {
            throw new IllegalArgumentException("Cannot classify list $list. Only ListWithTotalAndTypeWrapper is currently supported")
        }

        if (list.list instanceof DetachedListWithTotalAndType) {
            classified(list.list as DetachedListWithTotalAndType<T>)
        } else {
            throw new IllegalArgumentException("Cannot classify list $list. Only wrappers of DetachedListWithTotalAndType are supported")
        }

        return list
    }

    public <T> ListWithTotalAndType<T> classified(ListWithTotalAndType<T> list) {
        if (!(list instanceof DetachedListWithTotalAndType)) {
            throw new IllegalArgumentException("Cannot classify list $list. Only DetachedListWithTotalAndType is currently supported")
        }

        classified(list.criteria)

        return list
    }

    public <T> DetachedCriteria<T> classified (DetachedCriteria<T> criteria) {
        if (criteria.persistentEntity.javaClass == Classification) {
            return criteria
        }

        if (classificationsInUse) {
            return criteria
        }

        if (CatalogueElement.isAssignableFrom(criteria.persistentEntity.javaClass)) {
            criteria.incomingRelationships {
                'eq' 'relationshipType', RelationshipType.classificationType
                'in' 'source', classificationsInUse
            }
        } else if (Relationship.isAssignableFrom(criteria.persistentEntity.javaClass)) {
            criteria.or {
                'in'('classification',  classificationsInUse)
                isNull('classification')
            }
        }
        criteria
    }

    public <T> DetachedCriteria<T> classified (Class<T> resource) {
        classified(new DetachedCriteria<T>(resource))
    }

    public List<Classification> getClassificationsInUse() {
        if (!modelCatalogueSecurityService.isUserLoggedIn()) {
            return Collections.emptyList()
        }

        if (!modelCatalogueSecurityService.currentUser) {
            return Collections.emptyList()
        }

        if (!modelCatalogueSecurityService.currentUser.classifications) {
            return Collections.emptyList()
        }
        modelCatalogueSecurityService.currentUser.classifications
    }
}
