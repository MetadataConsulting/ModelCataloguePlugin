package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
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
}
