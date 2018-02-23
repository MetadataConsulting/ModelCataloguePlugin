package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.MetadataRoles
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.security.UserService
import org.springframework.context.MessageSource

class UserGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional
    User findById(long id) {
        User.get(id)
    }

    @Transactional(readOnly = true)
    List<User> findAll(Map args) {
        DetachedCriteria<User> query = User.where {}
        query.list(args)
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

        if (user.authorities.contains(MetadataRoles.ROLE_SUPERVISOR)) {
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
    boolean exists(Long userId) {
        User.exists(userId)
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
            transactionStatus.setRollbackOnly()
        }
        user
    }

    @Transactional
    User saveWithNameAndUsernameAndPasswordAndStatus(String name, String username, String password, ElementStatus status) {
        User userInstance = new User(name: name, username: username, password: password, status: status)
        save(userInstance)
    }

    protected DetachedCriteria<User> findQueryByNameOrUsername(String nameParam, String usernameParam) {
        User.where { username == usernameParam || name == nameParam }
    }

    protected DetachedCriteria<User> findQueryByUsername(String usernameParam) {
        User.where { username == usernameParam }
    }

    @Transactional(readOnly = true)
    String findApiKeyByUsername(String username) {
        findQueryByUsername(username).projections {
            property('apiKey')
        }.get()
    }

    @Transactional
    void updateApiKeyByUsername(String username, String apiKey) {
        DetachedCriteria<User> query = findQueryByUsername(username)
        query.updateAll(apiKey: apiKey)
    }

    @Transactional(readOnly = true)
    User findByEmail(String email) {
        findQueryByEmail(email).get()
    }

    DetachedCriteria<User> findQueryByEmail(String emailParam) {
        User.where { email == emailParam }
    }

    @Transactional(readOnly = true)
    Number countByEnabled(boolean enabled) {
        queryByEnabled(enabled).count()
    }

    DetachedCriteria<User> queryByEnabled(boolean enabledParam) {
        User.where { enabled == enabledParam}
    }

    @Transactional(readOnly = true)
    Number countByEnabledAndUsernameNotInList(boolean enabled, List<String> usernameList) {
        queryByEnabledAndUsernameNotInList(enabled, usernameList).count()
    }

    DetachedCriteria<User> queryByEnabledAndUsernameNotInList(boolean enabled, List<String> usernameList) {
        DetachedCriteria<User> query = queryByEnabled(enabled)
        query.where { user.username in usernameList }
    }
}
