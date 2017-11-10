package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.dataarchitect.ColumnTransformationDefinition
import org.modelcatalogue.core.dataarchitect.CsvTransformation

class ColumnTransformationDefinitionGormService {

    @Transactional
    ColumnTransformationDefinition saveByTransformationAndSourceAndDestinationAndHeader(CsvTransformation transformation, DataElement source, DataElement destination, String header) {
        ColumnTransformationDefinition instance  = new ColumnTransformationDefinition(source: source, transformation: transformation, destination: destination, header: header)
        save(instance)
    }

    @Transactional
    ColumnTransformationDefinition save(ColumnTransformationDefinition instance) {
        if ( !instance.save() ) {
            log.error 'could not save ColumnTransformationDefinition'
        }
        instance
    }

    @Transactional
    ColumnTransformationDefinition saveByTransformationAndSourceAndHeader(CsvTransformation transformation, DataElement source, String header) {
        ColumnTransformationDefinition instance  = new ColumnTransformationDefinition(source: source, transformation: transformation, header: header)
        save(instance)
    }
}
