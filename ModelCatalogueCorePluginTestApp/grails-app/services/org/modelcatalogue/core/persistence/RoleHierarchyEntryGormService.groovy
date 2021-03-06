package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.security.RoleHierarchyEntry
import org.slf4j.Logger
import org.springframework.context.MessageSource

@Slf4j
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

    @Transactional(readOnly = true)
    RoleHierarchyEntry findByEntry(String entry) {
        queryByEntry(entry).get()
    }

    DetachedCriteria<RoleHierarchyEntry> queryByEntry(String entryParam) {
        RoleHierarchyEntry.where {
            entry == entryParam
        }
    }

    @Override
    Logger getLog() {
        return log
    }
}
