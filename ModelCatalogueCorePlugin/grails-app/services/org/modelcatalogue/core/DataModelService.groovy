package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.actions.Batch
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.DetachedListWithTotalAndType
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.ListWithTotalAndTypeWrapper
import org.modelcatalogue.core.util.ListWrapper

class DataModelService {

    static transactional = false

    def modelCatalogueSecurityService

    public Map<String, Integer> getStatistics(DataModelFilter filter) {
        def model = [:]

        List<Class> displayed = [DataModel, DataClass, DataElement, DataType, MeasurementUnit, Asset]

        for (Class type in displayed) {
            DetachedCriteria criteria = classified(type, filter)
            criteria.projections {
                property 'status'
                property 'id'
            }
            criteria.inList('status', [ElementStatus.DRAFT, ElementStatus.FINALIZED])


            int draft = 0
            int finalized = 0

            for (Object[] row in criteria.list()) {
                ElementStatus status = row[0] as ElementStatus
                if (status == ElementStatus.DRAFT) draft++
                else if (status == ElementStatus.FINALIZED) finalized++
            }

            model["draft${type.simpleName}Count"]       = draft
            model["finalized${type.simpleName}Count"]   = finalized
            model["total${type.simpleName}Count"]       = draft + finalized
        }

        model.putAll([
                activeBatchCount:Batch.countByArchived(false),
                archivedBatchCount:Batch.countByArchived(true),
                relationshipTypeCount:RelationshipType.count(),
                transformationsCount:CsvTransformation.count()
        ])
        model
    }

    public <T> ListWrapper<T> classified(ListWrapper<T> list, DataModelFilter modelFilter = dataModelFilter) {
        if (!(list instanceof ListWithTotalAndTypeWrapper)) {
            throw new IllegalArgumentException("Cannot classify list $list. Only ListWithTotalAndTypeWrapper is currently supported")
        }

        if (list.list instanceof DetachedListWithTotalAndType) {
            classified(list.list as DetachedListWithTotalAndType<T>, modelFilter)
        } else {
            throw new IllegalArgumentException("Cannot classify list $list. Only wrappers of DetachedListWithTotalAndType are supported")
        }

        return list
    }

    public <T> ListWithTotalAndType<T> classified(ListWithTotalAndType<T> list, DataModelFilter modelFilter = dataModelFilter) {
        if (!(list instanceof DetachedListWithTotalAndType)) {
            throw new IllegalArgumentException("Cannot classify list $list. Only DetachedListWithTotalAndType is currently supported")
        }

        classified(list.criteria, modelFilter)

        return list
    }

    public <T> DetachedCriteria<T> classified(DetachedCriteria<T> criteria, DataModelFilter modelFilter = dataModelFilter) {
        if (criteria.persistentEntity.javaClass == DataModel) {
            return criteria
        }

        if (!modelFilter) {
            return criteria
        }

        if (modelFilter.unclassifiedOnly) {
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
                    if (modelFilter.excludes) {
                        not {
                            'in' 'id', modelFilter.excludes
                        }
                    }
                    if (modelFilter.includes) {
                        'in'  'id', modelFilter.includes
                    }
                }

            }
        } else if (Relationship.isAssignableFrom(criteria.persistentEntity.javaClass)) {
            criteria.or {
                and {
                    if (modelFilter.excludes) {
                        not {
                            'in' 'dataModel.id', modelFilter.excludes
                        }
                    }
                    if (modelFilter.includes) {
                        'in'  'dataModel.id', modelFilter.includes
                    }
                }
                isNull('dataModel')
            }
        }
        criteria
    }

    public <T> DetachedCriteria<T> classified(Class<T> resource, DataModelFilter modelFilter = dataModelFilter) {
        classified(new DetachedCriteria<T>(resource), modelFilter)
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
