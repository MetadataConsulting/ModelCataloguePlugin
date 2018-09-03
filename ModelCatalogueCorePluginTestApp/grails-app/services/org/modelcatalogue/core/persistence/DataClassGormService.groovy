package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dashboard.SearchQuery
import org.slf4j.Logger
import org.springframework.context.MessageSource

@Slf4j
class DataClassGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    Number updateTopLevel(List<Long> ids, boolean topLevel) {
        if (!ids) {
            return 0
        }
        DataClass.where { id in ids }.updateAll(topLevel: topLevel)
    }

    @Transactional
    Number updateTopLevel(Long dataClassId, boolean topLevel) {
        updateTopLevel([dataClassId], topLevel)
    }

    @Transactional(readOnly = true)
    DataClass findById(long id) {
        DataClass.get(id)
    }

    @Transactional(readOnly = true)
    List<DataClass> findAllByDataModel(DataModel dataModel, Integer offset = null, Integer max = null) {
        DetachedCriteria<DataClass> query = findQueryByDataModel(dataModel)

        if ( offset != null && max != null) {
            return  query.list(max: max, offset: offset)
        }

        findQueryByDataModel(dataModel).list()
    }

    @Transactional(readOnly = true)
    Number countByDataModel(DataModel dataModel) {
        findQueryByDataModel(dataModel).count()
    }

    protected DetachedCriteria<DataClass> findQueryByDataModel(DataModel dataModelParam) {
        DataClass.where { dataModel == dataModelParam }
    }

    @Transactional(readOnly = true)
    Number countByDataModelAndKeywordList(DataModel dataModel, List<String> keywords) {
        HqlOperation hqlOperation = HqlOperationUtils.ofDataModelAndKeywordList(dataModel, keywords)
        String hql = "select count(*) ${hqlOperation.hql}"
        log.debug 'about to execute query: {} with params: {}', hql, hqlOperation.params
        def result = DataClass.executeQuery(hql, hqlOperation.params)
        result[0] as Number
    }

    @Transactional(readOnly = true)
    List<DataClass> findAllByDataModelAndKeywordList(DataModel dataModel, List<String> keywords, Integer offset = null, Integer max = null) {
        HqlOperation hqlOperation = HqlOperationUtils.ofDataModelAndKeywordList(dataModel, keywords)
        log.debug 'about to execute query: {} with params: {}', hqlOperation.hql, hqlOperation.params
        if ( offset != null && max != null ) {
            return DataClass.findAll(hqlOperation.hql, hqlOperation.params, [offset: offset, max: max])
        }
        return DataClass.findAll(hqlOperation.hql, hqlOperation.params)
    }

    @Transactional
    DataClass saveWithNameAndDataModel(String name, DataModel dataModel) {
        save(new DataClass(name: name, dataModel: dataModel))
    }

    @Transactional
    DataClass saveWithNameAndDescription(String name, String description) {
        save(new DataClass(name: name, description: description))
    }

    @Transactional
    DataClass save(DataClass dataClassInstance) {
        if ( !dataClassInstance.save() ) {
            warnErrors(dataClassInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        dataClassInstance
    }

    @Transactional
    DataClass saveWithNameAndDescriptionAndStatus(String name, String description, ElementStatus status) {
        save(new DataClass(name: name, description: description, status: status))
    }

    @Transactional(readOnly = true)
    Number countByDataModelAndSearchStatusQuery(Long dataModelId, SearchQuery searchStatusQuery) {
        findQueryByDataModelAndSearchStatusQuery(dataModelId, searchStatusQuery).count()
    }

    DetachedCriteria<DataClass> findQueryByDataModelAndSearchStatusQuery(Long dataModelId, SearchQuery searchStatusQuery) {
        DetachedCriteria<DataClass> query = DataClass.where {}
        if ( dataModelId ) {
            query = query.where { dataModel == DataModel.load(dataModelId) }
        }
        if ( searchStatusQuery.statusList ) {
            query = query.where { status in searchStatusQuery.statusList }
        }
        if ( searchStatusQuery.search ) {
            String term = "%${searchStatusQuery.search}%".toString()
            query = query.where { name =~ term }
        }
        query
    }

    DetachedCriteria<DataClass> queryByIds(List<Long> ids) {
        DataClass.where { id in ids }
    }

    DetachedCriteria<DataClass> queryByName(String nameParam) {
        DataClass.where { name == nameParam }
    }

    @Transactional(readOnly = true)
    List<DataClass> findAllByIds(List<Long> ids) {
        if ( !ids ) {
            return [] as List<DataClass>
        }
        queryByIds(ids).list()
    }

    @Transactional
    void delete(DataClass dataClass) {
        dataClass?.delete()
    }

    @Transactional
    void deleteByName(String name) {
        DataClass dataClass = queryByName(name).get()
        dataClass?.delete()
    }

    @Transactional(readOnly = true)
    Integer count() {
        DataClass.count()
    }

    @Override
    Logger getLog() {
        return log
    }
}
