package org.modelcatalogue.core.security

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileStatic

@CompileStatic
class RoleGormService {

    @Transactional(readOnly = true)
    Role findByAuthority(String authority) {
        findQueryByAuthority(authority).get()
    }

    protected DetachedCriteria<Role> findQueryByAuthority(String authorityParam) {
        Role.where { authority == authorityParam }
    }

    @Transactional
    Role saveByAuthority(String authority) {
        Role role = new Role(authority: authority)
        if ( !role.save() ) {
            log.error('unable to save role')
        }
        role
    }
}
