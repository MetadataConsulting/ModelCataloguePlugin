package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionParameter
import org.springframework.context.MessageSource

class ActionParameterGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    ActionParameter saveWithActionAndNameAndExtensionValue(Action action, String name, String extensionValue) {
        ActionParameter actionParameterInstance = new ActionParameter(action: action, name: name, extensionValue: extensionValue)
        if ( !actionParameterInstance.save() ) {
            warnErrors(actionParameterInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        actionParameterInstance
    }


    protected DetachedCriteria<ActionParameter> findQueryByNameAndExtensionValueAndBatch(String name, String extensionValue) {

    }
}

