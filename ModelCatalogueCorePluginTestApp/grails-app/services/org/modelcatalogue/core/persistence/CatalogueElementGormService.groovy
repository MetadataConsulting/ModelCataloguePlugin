package org.modelcatalogue.core.persistence

import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.springframework.transaction.annotation.Transactional

@CompileStatic
class CatalogueElementGormService {

    @Transactional
    CatalogueElement findById(long id) {
        CatalogueElement.get(id)
    }
}
