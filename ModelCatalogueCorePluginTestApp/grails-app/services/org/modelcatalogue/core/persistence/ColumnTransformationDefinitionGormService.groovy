package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.dataarchitect.ColumnTransformationDefinition
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.springframework.context.MessageSource

class ColumnTransformationDefinitionGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    ColumnTransformationDefinition saveByTransformationAndSourceAndDestinationAndHeader(CsvTransformation transformation, DataElement source, DataElement destination, String header) {
        save(new ColumnTransformationDefinition(source: source, transformation: transformation, destination: destination, header: header))
    }

    @Transactional
    ColumnTransformationDefinition saveByTransformationAndSourceAndDestination(CsvTransformation transformation, DataElement source, DataElement destination) {
        save(new ColumnTransformationDefinition(source: source, transformation: transformation, destination: destination))
    }

    @Transactional
    ColumnTransformationDefinition save(ColumnTransformationDefinition instance) {
        if (!instance.save()) {
            warnErrors(instance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        instance
    }

    @Transactional
    ColumnTransformationDefinition saveByTransformationAndSourceAndHeader(CsvTransformation transformation, DataElement source, String header) {
        ColumnTransformationDefinition instance = new ColumnTransformationDefinition(source: source, transformation: transformation, header: header)
        save(instance)
    }

    @Transactional(readOnly = true)
    List<ColumnTransformationDefinition> findAllBySourceOrDestination(DataElement source, DataElement destination) {
        findQueryBySourceOrDestination(source, destination).list()
    }

    DetachedCriteria<ColumnTransformationDefinition> findQueryBySourceOrDestination(DataElement sourceParam, DataElement destinationParam) {
        ColumnTransformationDefinition.where { source == sourceParam && destination == destinationParam }
    }
}