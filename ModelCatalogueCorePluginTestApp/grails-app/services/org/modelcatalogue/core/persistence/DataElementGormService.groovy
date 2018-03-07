package org.modelcatalogue.core.persistence

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dashboard.SearchStatusQuery
import org.modelcatalogue.core.util.SortQuery
import org.springframework.context.MessageSource
import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.DataElement

@Slf4j
class DataElementGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional(readOnly = true)
    DataElement findById(long id) {
        DataElement.get(id)
    }

    @Transactional(readOnly = true)
    DataElement findByName(String name) {
        findQueryByName(name).get()
    }

    @Transactional
    DataElement saveByNameAndPrimitiveType(String name, DataType dataType) {
        save(new DataElement(name: name, dataType: dataType))
    }

    @Transactional
    DataElement save(DataElement dataElementInstance) {
        if ( !dataElementInstance.save() ) {
            warnErrors(dataElementInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        dataElementInstance
    }

    @Transactional
    DataElement saveWithNameAndDescription(String name, String description) {
        save(new DataElement(name: name, description: description))
    }

    @Transactional
    DataElement saveWithNameAndDescriptionAndDataType(String name, String description, DataType dataType) {
        save(new DataElement(name: name, description: description, dataType: dataType))
    }

    @Transactional
    DataElement saveWithNameAndDescriptionAndStatus(String name, String description, ElementStatus status) {
        save(new DataElement(name: name, description: description, status: status))
    }

    @Transactional
    DataElement saveWithNameAndDescriptionAndStatusAndDataType(String name, String description, ElementStatus status, DataType dataType) {
        save(new DataElement(name: name, description: description, status: status, dataType: dataType))
    }

    @Transactional
    DataElement saveWithNameAndDescriptionAndStatusAndDataModel(String name, String description, ElementStatus status, DataModel dataModel) {
        save(new DataElement(name: name, description: description, status: status, dataModel: dataModel))
    }

    protected DetachedCriteria<DataElement> findQueryByName(String nameParam) {
        DataElement.where { name == nameParam }
    }

    @Transactional(readOnly = true)
    List<DataElement> findAllByDataModel(DataModel dataModel, Integer offset = null, Integer max = null) {
        DetachedCriteria<DataElement> query = findQueryByDataModel(dataModel)

        if ( offset != null && max != null) {
            return  query.list(max: max, offset: offset)
        }

        findQueryByDataModel(dataModel).list()
    }

    @Transactional(readOnly = true)
    Number countByDataModel(DataModel dataModel) {
        findQueryByDataModel(dataModel).count()
    }

    protected DetachedCriteria<DataElement> findQueryByDataModel(DataModel dataModelParam) {
        DataElement.where { dataModel == dataModelParam }
    }

    @Transactional(readOnly = true)
    List<DataElement> findAllByDataType(DataType dataType) {
        findQueryByDataType(dataType).list()
    }

    @Transactional(readOnly = true)
    Number countByDataType(DataType dataType) {
        findQueryByDataType(dataType).count()
    }

    protected DetachedCriteria<DataElement> findQueryByDataType(DataType dataTypeParam) {
        DataElement.where {
            dataType == dataTypeParam
        }
    }

    @Transactional(readOnly = true)
    List<DataElement> findAllByDataTypeAndStatusInList(DataType dataType, List<ElementStatus> elementStatuses) {
        findQueryByDataTypeAndStatusInList(dataType, elementStatuses).list()
    }

    @Transactional(readOnly = true)
    Number countByDataTypeAndStatusInList(DataType dataType, List<ElementStatus> elementStatuses) {
        findQueryByDataTypeAndStatusInList(dataType, elementStatuses).count()
    }

    protected DetachedCriteria<DataElement> findQueryByDataTypeAndStatusInList(DataType dataTypeParam, List<ElementStatus> elementStatuses) {
        DataElement.where {
            dataType == dataTypeParam && status in elementStatuses
        }
    }

    @Transactional(readOnly = true)
    Number countByDataModelAndKeywordList(DataModel dataModel, List<String> keywords) {
        HqlOperation hqlOperation = HqlOperationUtils.ofDataModelAndKeywordList(dataModel, keywords)
        String hql = "select count(*) ${hqlOperation.hql}"
        log.debug 'about to execute query: {} with params: {}', hql, hqlOperation.params
        def result = DataElement.executeQuery(hql, hqlOperation.params)
        result[0] as Number
    }

    @Transactional(readOnly = true)
    List<DataElement> findAllByDataModelAndKeywordList(DataModel dataModel, List<String> keywords, Integer offset = null, Integer max = null) {
        HqlOperation hqlOperation = HqlOperationUtils.ofDataModelAndKeywordList(dataModel, keywords)
        log.debug 'about to execute query: {} with params: {}', hqlOperation.hql, hqlOperation.params
        if ( offset != null && max != null ) {
            return DataElement.findAll(hqlOperation.hql, hqlOperation.params, [offset: offset, max: max])
        }
        return DataElement.findAll(hqlOperation.hql, hqlOperation.params)
    }

    @CompileStatic
    DetachedCriteria<DataElement> findQueryBySearchStatusQuery(SearchStatusQuery searchStatusQuery) {
        DetachedCriteria<DataElement> query = DataElement.where {}
        if ( searchStatusQuery.statusList ) {
            query = query.where { status in searchStatusQuery.statusList }
        }
        if ( searchStatusQuery.search ) {
            String term = "%${searchStatusQuery.search}%".toString()
            query = query.where { name =~ term }
        }
        query
    }

    @CompileDynamic
    DetachedCriteria<DataElement> findQueryByDataModelAndSearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery, SortQuery sortQuery) {
        DetachedCriteria<DataElement> query = findQueryBySearchStatusQuery(searchStatusQuery)
        if ( dataModelId ) {
            query = query.where { dataModel == DataModel.load(dataModelId) }
        }
        if ( sortQuery?.sort != null && sortQuery?.order != null) {
            query = query.sort(sortQuery.sort, sortQuery.order)
        }
        query
    }

    @Transactional(readOnly = true)
    Number countByDataModelAndSearchStatusQuery(Long dataModelId, SearchStatusQuery searchStatusQuery) {
        findQueryBySearchStatusQuery(searchStatusQuery).count()
    }
}
