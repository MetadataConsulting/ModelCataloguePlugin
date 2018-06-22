package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.hibernate.SQLQuery
import org.hibernate.SessionFactory
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Tag
import org.modelcatalogue.core.ValidationRule
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.User

class CatalogueElementGormService {

    SessionFactory sessionFactory

    MeasurementUnitGormService measurementUnitGormService

    DataTypeGormService dataTypeGormService

    DataClassGormService dataClassGormService

    DataElementGormService dataElementGormService

    UserGormService userGormService

    TagGormService tagGormService

    AssetGormService assetGormService

    ValidationRuleGormService validationRuleGormService

    DataModelGormService dataModelGormService

    EnumeratedTypeGormService enumeratedTypeGormService

    static long hasElementsWhen = 0
    static Long hasElementsDataModelId = null
    static Boolean hasElementsResult = null

    Boolean hasElementsCached(Long dataModelId) {
        if (dataModelId == hasElementsDataModelId && (System.currentTimeMillis() - hasElementsWhen) < 60000) {
            System.out.println("CatalogueElementGormService.hasElementsCached(${dataModelId}) => ${hasElementsResult}")
            return hasElementsResult
        }
        return null
    }

    void setHasElementsCache(Long dataModelId, Boolean result) {
        hasElementsWhen = System.currentTimeMillis()
        hasElementsDataModelId = dataModelId
        hasElementsResult = result
    }

    @Transactional(readOnly = true)
    CatalogueElement findById(long id) {
        CatalogueElement.get(id)
    }

    @Transactional(readOnly = true)
    List<CatalogueElement> findAllByDataModel(DataModel dataModel) {
        findQueryByDataModel(dataModel).list()
    }

    @Transactional(readOnly = true)
    Boolean getHasElements(DataModel dataModel) {
        long now = System.currentTimeMillis()
        String query = "SELECT id FROM catalogue_element WHERE data_model_id = ${dataModel.id} LIMIT 1"
        final session = sessionFactory.currentSession
        final SQLQuery sqlQuery = session.createSQLQuery(query)
        Boolean result = sqlQuery.uniqueResult() != null
        System.out.println("CatalogueElementGormService.getHasElements(${dataModel.id}) => ${result} in ${(System.currentTimeMillis() - now)} ms")
        setHasElementsCache(dataModel.id, result)
        return result
    }

    Boolean hasElements(DataModel dataModel) {
        return hasElementsCached(dataModel.id) ?: getHasElements(dataModel)
    }

    @Transactional(readOnly = true)
    Number countByDataModel(DataModel dataModel) {
        String query = "SELECT COUNT(*) AS cnt FROM catalogue_element WHERE data_model_id = ${dataModel.id}"
        final session = sessionFactory.currentSession
        final SQLQuery sqlQuery = session.createSQLQuery(query)
        Integer result = sqlQuery.uniqueResult() as Integer
        System.out.println("CatalogueElementGormService.countByDataModel() => ${result}")
        return result
    }

    DetachedCriteria<CatalogueElement> findQueryByDataModel(DataModel dataModelParam) {
        CatalogueElement.where {
            dataModel == dataModelParam
        }
    }

    @Transactional
    CatalogueElement findCatalogueElementByClassAndId(Class catalogueElementClass, Long id) {
        if ( catalogueElementClass == DataModel ) {
            return dataModelGormService.findById(id)

        } else if ( catalogueElementClass == MeasurementUnit) {
            return measurementUnitGormService.findById(id)

        } else if ( catalogueElementClass == DataType ) {
            return dataTypeGormService.findById(id)

        } else if ( catalogueElementClass == DataClass ) {
            return dataClassGormService.findById(id)

        } else if ( catalogueElementClass == DataElement) {
            return dataElementGormService.findById(id)

        } else if ( catalogueElementClass == User) {
            return userGormService.findById(id)

        } else if ( catalogueElementClass == Tag ) {
            return tagGormService.findById(id)

        } else if ( catalogueElementClass == Asset ) {
            return assetGormService.findById(id)

        } else if ( catalogueElementClass == ValidationRule ) {
            return validationRuleGormService.findById(id)

        } else if ( catalogueElementClass == DataModel ) {
            return dataModelGormService.findById(id)

        } else if ( catalogueElementClass == EnumeratedType ) {
            return enumeratedTypeGormService.findById(id)
        }
        return null
    }

    @Transactional
    void updateCatalogueElementStatus(CatalogueElement catalogueElement, ElementStatus status) {
            catalogueElement.status = status
            catalogueElement.save(flush: true)

    }

    @Transactional(readOnly = true)
    List<CatalogueElement> findAllByIds(List<Long> ids) {
        if ( !ids ) {
            return []
        }
        queryByIds(ids).list()
    }

    DetachedCriteria<CatalogueElement> queryByIds(List<Long> ids) {
        CatalogueElement.where { id in ids }
    }
}
