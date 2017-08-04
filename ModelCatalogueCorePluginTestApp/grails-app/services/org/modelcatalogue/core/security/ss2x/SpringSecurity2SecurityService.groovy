package org.modelcatalogue.core.security.ss2x

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.util.Holders
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.LogoutListeners
import org.modelcatalogue.core.SecurityService
import org.modelcatalogue.core.security.Role
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.security.UserRole
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

    boolean hasRole(String authority, DataModel dataModel) {
        if (!authority) {
            return true
        }
        Role role = Role.findByAuthority(authority)
        return isAuthorised(dataModel, role)
    }

    boolean hasRole(String role) {
        if (!role) {
            return true
        }
        String translated = role
        if (role == "VIEWER") {
            translated = "ROLE_USER,ROLE_METADATA_CURATOR,ROLE_ADMIN,ROLE_SUPERVISOR"
        }  else if (role == "CURATOR") {
            translated = "ROLE_METADATA_CURATOR,ROLE_ADMIN,ROLE_SUPERVISOR"
        } else if (role == "ADMIN") {
            translated = "ROLE_ADMIN,ROLE_SUPERVISOR"
        } else if (role == "SUPERVISOR") {
            translated = "ROLE_SUPERVISOR"
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

    @Override
    void logout(String username) {
        Holders.applicationContext.getBean('userCache').removeUserFromCache(username)
    }

    boolean isSubscribed(DataModel dataModel) {
        if(!dataModel) return false
        UserRole userRole = UserRole.findByUserAndDataModel(getCurrentUser(), dataModel)

        if(userRole && dataModel){
            return true
        }

        return false
    }


    boolean isSubscribed(Set<Long> dataModelIds) {

        Boolean subscribed = false
        if(!dataModelIds) return subscribed

        dataModelIds.each{ dataModelId ->
            DataModel dataModel = DataModel.get(dataModelId)
            UserRole userRole
            if(dataModel) userRole = UserRole.findByUserAndDataModel(getCurrentUser(), dataModel)
            if(userRole && dataModel){
                subscribed = true
            }
        }

        return subscribed
    }

    boolean isSubscribed(CatalogueElement ce){
        if(!ce?.dataModel) return false
        UserRole userRole = UserRole.findByUserAndDataModel(getCurrentUser(), ce?.dataModel)

        if(userRole && ce?.dataModel){
            return true
        }

        return false
    }

    boolean isAuthorised(DataModel dataModel, Role role) {
        if(!dataModel) return true
        UserRole userRole = UserRole.findAllByUserAndDataModelAndRole(getCurrentUser(), dataModel, role)

        if(userRole){
            return true
        }
        return false
    }


    List<DataModel> getSubscribed(){

        List<UserRole> userRoles = UserRole.findAllByUser(getCurrentUser())
        List<DataModel> dataModels = []
        dataModels = userRoles.findResults{it.dataModel}
        return dataModels

    }

    Set getRoles(String dataModelId){

        DataModel dataModel = DataModel.get(dataModelId)

        if(dataModel){
            Set<UserRole> userRoles = UserRole.findAllByUserAndDataModel(getCurrentUser(), dataModel)
            return userRoles.collect{it.role.authority}
        }

        currentUser.authorities*.authority

    }

    void addUserRoleModel(User user, Role role, DataModel model){
        UserRole.create user, role, model
    }

    void removeUserRoleModel(User user, Role role, DataModel model){
        UserRole.remove user, role, model
    }

    void removeAllUserRoleModel(User user, DataModel model){
        UserRole.executeUpdate 'DELETE FROM UserRole WHERE user=:user AND dataModel=:dataModel', [user: user, dataModel: model]
    }

}
