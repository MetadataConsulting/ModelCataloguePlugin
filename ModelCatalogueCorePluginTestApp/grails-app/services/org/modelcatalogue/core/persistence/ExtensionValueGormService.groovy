package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.WarnGormErrors
import org.springframework.context.MessageSource

class ExtensionValueGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    ExtensionValue save(ExtensionValue extensionValueInstance) {
        if ( !extensionValueInstance.save() ) {
            warnErrors(extensionValueInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        extensionValueInstance
    }

    @Transactional
    ExtensionValue saveWithNameAndExtensionValueAndDataElement(String name, String extensionValue, DataElement dataElement) {
        save(new ExtensionValue(name: name, extensionValue: extensionValue, element: dataElement))
    }

    @Transactional
    void deleteByElement(CatalogueElement elementName ) {
        ExtensionValue.where { element == elementName }.deleteAll()
    }

    @Transactional(readOnly = true)
    ExtensionValue findById(Long id) {
        ExtensionValue.get(id)
    }

    DetachedCriteria<ExtensionValue> queryByIds(List<Long> ids) {
        ExtensionValue.where { id in ids }
    }

    @Transactional(readOnly = true)
    List<ExtensionValue> findAllByIds(List<Long> ids) {
        if ( !ids ) {
            return [] as List<ExtensionValue>
        }
        queryByIds(ids).list()
    }

    @Transactional(readOnly = true)
    Integer count() {
        ExtensionValue.count()
    }

}
