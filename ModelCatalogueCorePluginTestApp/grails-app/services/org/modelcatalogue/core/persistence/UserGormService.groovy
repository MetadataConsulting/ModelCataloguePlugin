package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.security.UserService
import org.springframework.context.MessageSource
import org.springframework.transaction.interceptor.TransactionAspectSupport

class UserGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    User findById(long id) {
        User.get(id)
    }

    /**
     *
     * @param userId
     * @param enabled
     * @return null if the user is not found, user with or without erros after the updates
     */
    @Transactional
    User switchEnabled(long userId, boolean enabled) {
        User user = findById(userId)
        if (!user) {
            return null
        }

        if (user.authorities.contains(UserService.ROLE_SUPERVISOR)) {
            user.errors.rejectValue('enabled', 'user.cannot.edit.supervisor', 'Cannot edit supervisor account')
            return user
        }

        user.enabled = enabled

        user.save(flush: true)
        user
    }

    @Transactional(readOnly = true)
    List<String> findAllUsername() {
        (User.where {}.projections {
            property('username')
        }.list() as List<String>).sort { String a, b ->
            a <=> b
        }
    }

    @Transactional(readOnly = true)
    User findByUsername(String username) {
        findQueryByUsername(username).get()
    }

    @Transactional(readOnly = true)
    User findByNameOrUsername(String name , String username) {
        findQueryByNameOrUsername(name, username).get()
    }

    @Transactional
    User save(User user) {
        if ( !user.save() ) {
            warnErrors(user, messageSource)
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
        }
        user
    }

    protected DetachedCriteria<User> findQueryByNameOrUsername(String nameParam, String usernameParam) {
        User.where { username == usernameParam || name == nameParam }
    }

    protected DetachedCriteria<User> findQueryByUsername(String usernameParam) {
        User.where { username == usernameParam }
    }

}
