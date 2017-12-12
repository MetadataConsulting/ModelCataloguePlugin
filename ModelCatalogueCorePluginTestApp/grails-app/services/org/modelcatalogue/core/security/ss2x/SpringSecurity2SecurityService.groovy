package org.modelcatalogue.core.security.ss2x

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.acl.AclUtilService
import grails.util.Holders
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.LogoutListeners
import org.modelcatalogue.core.SecurityService
import org.modelcatalogue.core.security.DataModelAclService
import org.modelcatalogue.core.security.MetadataRolesUtils
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.persistence.UserGormService
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.concurrent.TimeUnit

class SpringSecurity2SecurityService implements SecurityService, LogoutListeners, LogoutHandler {

    //TODO: How do we handle imports - this needs work

    static transactional = false

    SpringSecurityService springSecurityService

    DataModelAclService dataModelAclService

    UserGormService userGormService

    AclUtilService aclUtilService

    Cache<String, Long> lastSeenCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(1, TimeUnit.DAYS).build()

    boolean isUserLoggedIn() {
        return springSecurityService.isLoggedIn()
    }


    //check if a user is authorised for a particular model
    boolean hasRole(String authority, DataModel dataModel) {
        //if no role is passed, can't have that role
        if (!authority) {
            return false
        }
        Collection<String> roles = MetadataRolesUtils.getRolesFromAuthority(authority)
        if ( !SpringSecurityUtils.ifAnyGranted(roles.join(',')) ) {
            return false
        }
        dataModelAclService.isAdminOrHasReadPermission(dataModel)
    }

    //check if a user a general role
    //this is used for very general activities like creating models or viewing draft models

    boolean hasRole(String authority) {
        //if no role is passed, can't have that role
        if (!authority) {
            return false
        }
        String roles = MetadataRolesUtils.getRolesFromAuthority(authority).join(',')
        return SpringSecurityUtils.ifAnyGranted(roles)
    }

    String encodePassword(String password) {
        return springSecurityService.encodePassword(password)
    }

    User getCurrentUser() {
        User user = springSecurityService.currentUser
        if (!user) return user
        lastSeenCache.put(user.username, System.currentTimeMillis())
        return user as User
    }

    @Override
    void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        def id = authentication?.principal?.id
        if (id) {
            User user = userGormService.findById(id as Long)
            userLoggedOut(user)
        }
    }

    @Override
    Map<String, Long> getUsersLastSeen() {
        lastSeenCache.asMap()
    }

    @Override
    void logout(String username) {
        Holders.applicationContext.getBean('userCache').removeUserFromCache(username)
    }
}
