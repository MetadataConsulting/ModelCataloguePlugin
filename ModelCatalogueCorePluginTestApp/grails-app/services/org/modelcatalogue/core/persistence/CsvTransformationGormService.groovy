package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.springframework.context.MessageSource

class CsvTransformationGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    CsvTransformation findById(long id) {
        CsvTransformation.get(id)
    }

    @Transactional
    CsvTransformation saveByName(String name) {
        CsvTransformation transformation = new CsvTransformation(name: name)
        if ( !transformation.save() ) {
            warnErrors(transformation, messageSource)
            transactionStatus.setRollbackOnly()
        }
        transformation
    }

    DetachedCriteria<CsvTransformation> queryByIds(List<Long> ids) {
        CsvTransformation.where { id in ids }
    }

    @Transactional(readOnly = true)
    List<CsvTransformation> findAllByIds(List<Long> ids) {
        if ( !ids ) {
            return [] as List<CsvTransformation>
        }
        queryByIds(ids).list()
    }
}
