package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.query.api.BuildableCriteria
import org.hibernate.transform.Transformers
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.security.LastSeen
import org.modelcatalogue.core.security.UserAuthentication
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.SortQuery
import org.slf4j.Logger
import org.springframework.context.MessageSource

@Slf4j
class UserAuthenticationGormService implements WarnGormErrors {
    MessageSource messageSource

    @Transactional
    UserAuthentication save(String username) {
        if ( !username ) {
            return null
        }
        UserAuthentication userAuthentication = new UserAuthentication(username: username, authenticationDate: new Date())
        if ( !userAuthentication.save() ) {
            warnErrors(userAuthentication, messageSource)
            transactionStatus.setRollbackOnly()
        }
        userAuthentication
    }

    @CompileDynamic
    @Transactional(readOnly = true)
    List<LastSeen> findAllLatest(SortQuery sortQuery, PaginationQuery paginationQuery) {
        BuildableCriteria criteria = UserAuthentication.createCriteria()
        criteria.list {
            resultTransformer(Transformers.aliasToBean(LastSeen))
            maxResults(paginationQuery.max)
            firstResult(paginationQuery.offset)
            order(sortQuery.sort, sortQuery.order)
            projections {
                property('username', 'username')
                max('authenticationDate', 'authenticationDate')
                groupProperty 'username'
            }
        } as List<LastSeen>
    }

    Number countDistinct() {
        BuildableCriteria c = UserAuthentication.createCriteria()
        c.get {
            projections {
                countDistinct('username')
            }
        } as Number
    }

    @Override
    Logger getLog() {
        return log
    }

}
