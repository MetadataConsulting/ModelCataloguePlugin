package org.modelcatalogue.core.persistence

import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.springframework.transaction.annotation.Transactional

class CsvTransformationGormService {

    @Transactional
    CsvTransformation findById(long id) {
        CsvTransformation.get(id)
    }
}
