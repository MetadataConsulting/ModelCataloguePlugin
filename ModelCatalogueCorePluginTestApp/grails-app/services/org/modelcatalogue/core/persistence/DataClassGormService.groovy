package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.springframework.context.MessageSource

@Slf4j
class DataClassGormService implements WarnGormErrors {

    MessageSource messageSource

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
}
