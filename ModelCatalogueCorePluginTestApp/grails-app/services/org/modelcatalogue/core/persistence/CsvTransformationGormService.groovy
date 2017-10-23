package org.modelcatalogue.core.persistence

import groovy.transform.CompileStatic
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.springframework.transaction.annotation.Transactional

@CompileStatic
class CsvTransformationGormService {

    @Transactional
    CsvTransformation findById(long id) {
        CsvTransformation.get(id)
    }
}
