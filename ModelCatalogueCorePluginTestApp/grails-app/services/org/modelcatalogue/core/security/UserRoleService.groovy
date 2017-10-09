package org.modelcatalogue.core.security

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileStatic

@CompileStatic
class UserRoleService {

    @Transactional(readOnly = true)
    List<UserRole> findAllByUser(User userParam) {
        findQueryByUser(userParam).list()
    }

    DetachedCriteria<UserRole> findQueryByUser(User userParam) {
        UserRole.where { user == userParam }
    }
}
