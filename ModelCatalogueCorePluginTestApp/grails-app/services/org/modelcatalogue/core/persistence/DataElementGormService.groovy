package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.PrimitiveType

class DataElementGormService {

    @Transactional(readOnly = true)
    DataElement findByName(String name) {
        findQueryByName(name).get()
    }

    @Transactional
    DataElement saveByNameAndPrimitiveType(String name, PrimitiveType temperature) {
        DataElement dataElement = new DataElement(name: name, dataType: temperature)
        if ( !dataElement.save() ) {
            log.error 'data element cound not be saved'
        }
        dataElement
    }

    DetachedCriteria<DataElement> findQueryByName(String nameParam) {
        DataElement.where { name == nameParam }
    }

}
