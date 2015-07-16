package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.DetachedListWithTotalAndType
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.ListWithTotalAndTypeWrapper
import org.modelcatalogue.core.util.ListWrapper

class DataModelService {

    static transactional = false

    def modelCatalogueSecurityService

    public <T> ListWrapper<T> classified(ListWrapper<T> list, DataModelFilter classificationsFilter = dataModelFilter) {
        if (!(list instanceof ListWithTotalAndTypeWrapper)) {
            throw new IllegalArgumentException("Cannot classify list $list. Only ListWithTotalAndTypeWrapper is currently supported")
        }

        if (list.list instanceof DetachedListWithTotalAndType) {
            classified(list.list as DetachedListWithTotalAndType<T>, classificationsFilter)
        } else {
            throw new IllegalArgumentException("Cannot classify list $list. Only wrappers of DetachedListWithTotalAndType are supported")
        }

        return list
    }

    public <T> ListWithTotalAndType<T> classified(ListWithTotalAndType<T> list, DataModelFilter classificationsFilter = dataModelFilter) {
        if (!(list instanceof DetachedListWithTotalAndType)) {
            throw new IllegalArgumentException("Cannot classify list $list. Only DetachedListWithTotalAndType is currently supported")
        }

        classified(list.criteria, classificationsFilter)

        return list
    }

    public <T> DetachedCriteria<T> classified(DetachedCriteria<T> criteria, DataModelFilter classificationsFilter = dataModelFilter) {
        if (criteria.persistentEntity.javaClass == DataModel) {
            return criteria
        }

        if (!classificationsFilter) {
            return criteria
        }

        if (classificationsFilter.unclassifiedOnly) {
            criteria.not {
                // this should work (better) without calling the .list()
                // but at the moment we're getting ConverterNotFoundException
                'in' 'id', new DetachedCriteria<Relationship>(Relationship).build {
                    projections { property 'destination.id' }
                    eq 'relationshipType', RelationshipType.declarationType
                }.list()
            }
            return criteria
        }

        if (CatalogueElement.isAssignableFrom(criteria.persistentEntity.javaClass)) {
            criteria.incomingRelationships {
                'eq' 'relationshipType', RelationshipType.declarationType
                source {
                    if (classificationsFilter.excludes) {
                        not {
                            'in' 'id', classificationsFilter.excludes
                        }
                    }
                    if (classificationsFilter.includes) {
                        'in'  'id', classificationsFilter.includes
                    }
                }

            }
        } else if (Relationship.isAssignableFrom(criteria.persistentEntity.javaClass)) {
            criteria.or {
                and {
                    if (classificationsFilter.excludes) {
                        not {
                            'in' 'classification.id', classificationsFilter.excludes
                        }
                    }
                    if (classificationsFilter.includes) {
                        'in'  'classification.id', classificationsFilter.includes
                    }
                }
                isNull('classification')
            }
        }
        criteria
    }

    public <T> DetachedCriteria<T> classified(Class<T> resource, DataModelFilter classificationsFilter = dataModelFilter) {
        classified(new DetachedCriteria<T>(resource), classificationsFilter)
    }

    public DataModelFilter getDataModelFilter() {
        if (!modelCatalogueSecurityService.isUserLoggedIn()) {
            return DataModelFilter.NO_FILTER
        }

        if (!modelCatalogueSecurityService.currentUser) {
            return DataModelFilter.NO_FILTER
        }


        DataModelFilter.from(modelCatalogueSecurityService.currentUser)
    }
}
