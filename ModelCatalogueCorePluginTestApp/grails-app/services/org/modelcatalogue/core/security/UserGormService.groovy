package org.modelcatalogue.core.security

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional

class UserGormService {

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
            log.error('unable to save user')
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
