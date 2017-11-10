package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.PrimitiveType

class PrimitiveTypeGormService {

    @Transactional
    PrimitiveType save(PrimitiveType primitiveType) {
        if ( !primitiveType.save() ) {
            log.error('unable to save primitiveType')
        }
        primitiveType
    }
}
