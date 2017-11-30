package org.modelcatalogue.core.security.ss2x

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import grails.plugin.springsecurity.userdetails.NoStackUsernameNotFoundException
import grails.transaction.Transactional
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

class GormWithEmailUserDetailsService extends GormUserDetailsService {


    @Transactional(readOnly=true, noRollbackFor=[IllegalArgumentException, UsernameNotFoundException])
    UserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException {
        def conf = SpringSecurityUtils.securityConfig
        String userClassName = conf.userLookup.userDomainClassName
        def dc = grailsApplication.getDomainClass(userClassName)
        if (!dc) {
            throw new IllegalArgumentException("The specified user domain class '$userClassName' is not a domain class")
        }

        Class<?> User = dc.clazz

        def user = User.findByUsername(username, [sort: 'versionNumber', order: 'desc'])


        if (!user) {
            user = User.findByEmail(username, [sort: 'versionNumber', order: 'desc'])
        }

        if (!user) {
            log.warn "User not found: $username"
            throw new NoStackUsernameNotFoundException()
        }

        Collection<GrantedAuthority> authorities = loadAuthorities(user, username, loadRoles)
        createUserDetails user, authorities
    }
}
