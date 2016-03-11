package org.modelcatalogue.core

import com.google.common.collect.ImmutableMap
import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.actions.Batch
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.DetachedListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWithTotalAndTypeWrapper
import org.modelcatalogue.core.util.lists.ListWrapper

import javax.annotation.PostConstruct

class DataModelService {

    static transactional = false

    def modelCatalogueSecurityService
    def grailsApplication

    private boolean legacyDataModels

    @PostConstruct
    void init() {
        this.legacyDataModels = grailsApplication.config.mc.legacy.dataModels
    }

    public Map<String, Integer> getStatistics(DataModelFilter filter) {
        def model = [:]

        List<Class> displayed = [DataModel, DataClass, DataElement, DataType, MeasurementUnit, Asset]

        for (Class type in displayed) {
            DetachedCriteria criteria = classified(type, filter)
            criteria.projections {
                property 'status'
                property 'id'
            }
            criteria.inList('status', [ElementStatus.DRAFT, ElementStatus.FINALIZED, ElementStatus.DEPRECATED, ElementStatus.PENDING])


            int draft = 0
            int finalized = 0
            int deprecated = 0
            int pending = 0

            for (Object[] row in criteria.list()) {
                ElementStatus status = row[0] as ElementStatus
                if (status == ElementStatus.DRAFT) draft++
                else if (status == ElementStatus.FINALIZED) finalized++
                else if (status == ElementStatus.DEPRECATED) deprecated++
                else if (status == ElementStatus.PENDING) pending++
            }

            model["draft${type.simpleName}Count"]       = draft
            model["finalized${type.simpleName}Count"]   = finalized
            model["pending${type.simpleName}Count"]     = pending
            model["deprecated${type.simpleName}Count"]  = deprecated
            model["total${type.simpleName}Count"]       = draft + finalized + pending + deprecated
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

    public <T> DetachedCriteria<T> classified(DetachedCriteria<T> criteria) {
        classified(criteria, dataModelFilter)
    }

    public static <T> DetachedCriteria<T> classified(DetachedCriteria<T> criteria, DataModelFilter modelFilter) {
        if (criteria.persistentEntity.javaClass == DataModel) {
            return criteria
        }

        if (!modelFilter) {
            return criteria
        }

        if (modelFilter.unclassifiedOnly) {
            criteria.isNull('dataModel')
            return criteria
        }

        if (CatalogueElement.isAssignableFrom(criteria.persistentEntity.javaClass)) {
            if (modelFilter.includes) {
                criteria.'in' 'dataModel.id', modelFilter.includes
            } else if (modelFilter.excludes) {
                criteria.not {
                    'in' 'dataModel.id', modelFilter.excludes
                }
            }
        } else if (Relationship.isAssignableFrom(criteria.persistentEntity.javaClass)) {
            criteria.or {
                and {
                    if (modelFilter.includes) {
                        criteria.'in' 'dataModel.id', modelFilter.includes
                    } else if (modelFilter.excludes) {
                        criteria.not {
                            'in' 'dataModel.id', modelFilter.excludes
                        }
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

    public boolean isLegacyDataModels() {
        return this.legacyDataModels
    }
}
