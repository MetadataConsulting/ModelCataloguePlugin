package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.dataarchitect.CsvTransformation

class CsvTransformationGormService {

    @Transactional
    CsvTransformation findById(long id) {
        CsvTransformation.get(id)
    }

    @Transactional
    CsvTransformation saveByName(String name) {
        CsvTransformation transformation = new CsvTransformation(name: name)
        if ( !transformation.save() ) {
            log.error 'unable to save transformation'
        }
        transformation
    }
}
