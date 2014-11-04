package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.util.DetachedListWithTotalAndType
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.ListWithTotalAndTypeWrapper
import org.modelcatalogue.core.util.ListWrapper

class ClassificationService {

    static transactional = false

    def modelCatalogueSecurityService

    public <T> ListWrapper<T> classified(ListWrapper<T> list, Collection<Classification> classifications = classificationsInUse) {
        if (!(list instanceof ListWithTotalAndTypeWrapper)) {
            throw new IllegalArgumentException("Cannot classify list $list. Only ListWithTotalAndTypeWrapper is currently supported")
        }

        if (list.list instanceof DetachedListWithTotalAndType) {
            classified(list.list as DetachedListWithTotalAndType<T>, classifications)
        } else {
            throw new IllegalArgumentException("Cannot classify list $list. Only wrappers of DetachedListWithTotalAndType are supported")
        }

        return list
    }

    public <T> ListWithTotalAndType<T> classified(ListWithTotalAndType<T> list, Collection<Classification> classifications = classificationsInUse) {
        if (!(list instanceof DetachedListWithTotalAndType)) {
            throw new IllegalArgumentException("Cannot classify list $list. Only DetachedListWithTotalAndType is currently supported")
        }

        classified(list.criteria, classifications)

        return list
    }

    public <T> DetachedCriteria<T> classified(DetachedCriteria<T> criteria, Collection<Classification> classifications = classificationsInUse) {
        if (criteria.persistentEntity.javaClass == Classification) {
            return criteria
        }

        if (!classifications) {
            return criteria
        }

        if (CatalogueElement.isAssignableFrom(criteria.persistentEntity.javaClass)) {
            criteria.incomingRelationships {
                'eq' 'relationshipType', RelationshipType.classificationType
                'in' 'source', classifications
            }
        } else if (Relationship.isAssignableFrom(criteria.persistentEntity.javaClass)) {
            criteria.or {
                'in'('classification', classifications)
                isNull('classification')
            }
        }
        criteria
    }

    public <T> DetachedCriteria<T> classified(Class<T> resource, Collection<Classification> classifications = classificationsInUse) {
        classified(new DetachedCriteria<T>(resource), classifications)
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
