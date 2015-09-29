package org.modelcatalogue.core.security.ss2x

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import org.modelcatalogue.core.LogoutListeners
import org.modelcatalogue.core.SecurityService
import org.modelcatalogue.core.security.User
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.concurrent.TimeUnit

class SpringSecurity2SecurityService implements SecurityService, LogoutListeners, LogoutHandler {

    static transactional = false

    SpringSecurityService springSecurityService

    Cache<String, Long> lastSeenCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(1, TimeUnit.DAYS).build()

    boolean isUserLoggedIn() {
        return springSecurityService.isLoggedIn()
    }

    boolean hasRole(String role) {
        String translated = role
        if (role == "VIEWER") {
            translated = "ROLE_USER,ROLE_METADATA_CURATOR,ROLE_ADMIN"
        }  else if (role == "CURATOR") {
            translated = "ROLE_METADATA_CURATOR,ROLE_ADMIN"
        } else if (role == "ADMIN") {
            translated = "ROLE_ADMIN"
        } else if (!translated.startsWith('ROLE_')) {
            translated = "ROLE_${translated}"
        }
        return SpringSecurityUtils.ifAnyGranted(translated)
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
            userLoggedOut(User.get(id as Long))
        }
    }

    @Override
    Map<String, Long> getUsersLastSeen() {
        lastSeenCache.asMap()
    }
}
