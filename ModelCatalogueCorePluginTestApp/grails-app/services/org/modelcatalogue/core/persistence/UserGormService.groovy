package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.security.UserService

class UserGormService {

    @Transactional
    User findByUsername(String usernameParam) {
        User.where { username == usernameParam }.get()
    }

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
}