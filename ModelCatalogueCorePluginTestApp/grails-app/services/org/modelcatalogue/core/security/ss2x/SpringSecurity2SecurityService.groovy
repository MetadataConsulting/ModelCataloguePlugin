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


    //TODO: How do we handle imports - this needs work

    static transactional = false

    SpringSecurityService springSecurityService

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
        String translated = getRolesFromAuthority(authority)
        Set<Role> roles = []
        translated.split(",").each{
            Role role = Role.findByAuthority(it)
            if(role) roles.add(role)
        }

        if(roles.size()==0) false

        //find if the user has a role for a model, i.e. if they are authorised, for any of the roles
        return isAuthorised(dataModel, roles)
    }

    //check if a user a general role
    //this is used for very general activities like creating models or viewing draft models

    boolean hasRole(String authority) {
        //if no role is passed, can't have that role
        if (!authority) {
            return false
        }
        String translated = getRolesFromAuthority(authority)
        return SpringSecurityUtils.ifAnyGranted(translated)
    }

    private String getRolesFromAuthority(String authority){
        String translated = authority
        if (authority == "VIEWER") {
            translated = "ROLE_USER,ROLE_METADATA_CURATOR,ROLE_ADMIN,ROLE_SUPERVISOR"
        }  else if (authority == "CURATOR") {
            translated = "ROLE_METADATA_CURATOR,ROLE_ADMIN,ROLE_SUPERVISOR"
        } else if (authority == "ADMIN") {
            translated = "ROLE_ADMIN,ROLE_SUPERVISOR"
        } else if (authority == "SUPERVISOR") {
            translated = "ROLE_SUPERVISOR"
        } else if (!translated.startsWith('ROLE_')) {
            translated = "ROLE_${translated}"
        }
        translated
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

//check if the user is subscribed to a catalogueElement
    boolean isSubscribed(CatalogueElement ce){

        //check if the user is a supervisor - if they are, they are subscribed to everything
        if(isSupervisor()) return true

        //if the catalogue element doesn't have a data model it is an orphan and you can't be subscribed to it.
        if(!ce?.dataModel) return false

        isSubscribed(ce?.dataModel)
    }

    //check if the user is a supervisor
    //if they are they can do most things

    boolean isSupervisor(){
        if(UserRole.findByUserAndRole(getCurrentUser(), Role.findByAuthority('ROLE_SUPERVISOR'))) return true
        return false
    }

    //check if a user has the a specific role for a data model
    boolean isAuthorised(DataModel dataModel, Set<Role> roles) {

        //check if the user is a supervisor - if they are, they are authorised to do everything
        if(isSupervisor()) return true

        //if there isn't a data model they cannot be authorised
        if(!dataModel) return false


        // if a user has any of the roles included then they are authorised
        boolean hasRole = false
        roles.any { Role role ->
            if (UserRole.findAllByUserAndDataModelAndRole(getCurrentUser(), dataModel, role)) {
                hasRole = true
                return true
            }
        }
        return hasRole
    }

    void addUserRoleModel(User user, Role role, DataModel model, boolean flush = false){
        UserRole.create user, role, model, flush
    }

    void removeUserRoleModel(User user, Role role, DataModel model){
        UserRole.remove user, role, model
    }

    void removeAllUserRoleModel(User user, DataModel model){
        UserRole.executeUpdate 'DELETE FROM UserRole WHERE user=:user AND dataModel=:dataModel', [user: user, dataModel: model]
    }

    void copyUserRoles(DataModel sourceModel, DataModel destinationModel){

        List<UserRole> userRolesSourceModel = UserRole.findAllByDataModel(sourceModel)
        userRolesSourceModel.each{ UserRole userRole ->
            addUserRoleModel userRole.user, userRole.role, destinationModel
        }
    }

}
