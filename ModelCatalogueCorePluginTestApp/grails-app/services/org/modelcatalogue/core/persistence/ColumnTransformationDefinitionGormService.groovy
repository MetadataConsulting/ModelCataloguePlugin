package org.modelcatalogue.core.persistence

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
        ColumnTransformationDefinition instance  = new ColumnTransformationDefinition(source: source, transformation: transformation, destination: destination, header: header)
        save(instance)
    }

    @Transactional
    ColumnTransformationDefinition save(ColumnTransformationDefinition instance) {
        if ( !instance.save() ) {
            warnErrors(instance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        instance
    }

    @Transactional
    ColumnTransformationDefinition saveByTransformationAndSourceAndHeader(CsvTransformation transformation, DataElement source, String header) {
        ColumnTransformationDefinition instance  = new ColumnTransformationDefinition(source: source, transformation: transformation, header: header)
        save(instance)
    }
}
