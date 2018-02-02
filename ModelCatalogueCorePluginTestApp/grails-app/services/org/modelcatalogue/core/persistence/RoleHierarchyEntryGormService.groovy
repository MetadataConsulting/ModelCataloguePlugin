package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.security.RoleHierarchyEntry
import org.springframework.context.MessageSource

class RoleHierarchyEntryGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional(readOnly = true)
    Number count() {
        RoleHierarchyEntry.count()
    }

    @Transactional
    RoleHierarchyEntry save(String entry) {
        RoleHierarchyEntry instance = new RoleHierarchyEntry(entry: entry)
        if ( !instance.save() ) {
            transactionStatus.setRollbackOnly()
            warnErrors(instance, messageSource)
        }
        instance
    }
}
