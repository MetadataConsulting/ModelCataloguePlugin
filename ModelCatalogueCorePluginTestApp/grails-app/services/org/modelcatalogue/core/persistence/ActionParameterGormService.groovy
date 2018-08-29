package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionParameter
import org.slf4j.Logger
import org.springframework.context.MessageSource

@Slf4j
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

    @Transactional(readOnly = true)
    List<ActionParameter> findAllByIds(List<Long> ids) {
        if ( !ids ) {
            return [] as List<ActionParameter>
        }
        ActionParameter.where { id in ids }.list()
    }

    protected DetachedCriteria<ActionParameter> findQueryByNameAndExtensionValueAndBatch(String name, String extensionValue) {

    }

    @Override
    Logger getLog() {
        return log
    }
}

