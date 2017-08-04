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
        Role role = Role.findByAuthority(authority)
        //find if the user has a role for a model, i.e. if they are authorised
        return isAuthorised(dataModel, role)
    }

    //check if a user a general role
    //this is used for very general activities like creating models or viewing draft models

    boolean hasRole(String authority) {
        //if no role is passed, can't have that role
        if (!authority) {
            return false
        }
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



    //check if the user is subscribed to a data model
    boolean isSubscribed(DataModel dataModel) {

        //check if the user is a supervisor - if they are, they are subscribed to everything
        if(isSupervisor()) return true

        //if no data model , then there's nothing to be subscribed to
        if(!dataModel) return false

        //otherwise check that a userrole exists with this user and this data model - if so you have a subscription otherwise you don't
        UserRole userRole = UserRole.findByUserAndDataModel(getCurrentUser(), dataModel)

        if(userRole && dataModel){
            return true
        }

        return false
    }


    //check if the user is subscribed to a list of data models
    boolean isSubscribed(Set<Long> dataModelIds) {

        Boolean subscribed = false

        //check if the user is a supervisor - if they are, they are subscribed to everything
        if(isSupervisor()) return true

        //if no data models have been included then there's nothing to be subscribed to
        if(!dataModelIds) return subscribed

        //otherwise check that a userrole exists for all the data models in the list and this data model - if so you have a subscription otherwise you don't
        dataModelIds.each{ dataModelId ->
            DataModel dataModel = DataModel.get(dataModelId)
            UserRole userRole
            if(dataModel) userRole = UserRole.findByUserAndDataModel(getCurrentUser(), dataModel)
            if(userRole && dataModel){
                subscribed = true
            }else{
                return false
            }
        }

        return subscribed
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
    boolean isAuthorised(DataModel dataModel, Role role) {

        //check if the user is a supervisor - if they are, they are authorised to do everything
        if(isSupervisor()) return true

        //if there isn't a data model they cannot be authorised
        if(!dataModel) return true

        //see if a user has the right authorisation for the model
        UserRole userRole = UserRole.findAllByUserAndDataModelAndRole(getCurrentUser(), dataModel, role)

        // if a user role exists with the role, user and data model they are authorised
        if(userRole) return true

        return false
    }

    //get all the data models that this user is subscribed to regardless of the role
    List<DataModel> getSubscribed(){
        //get all the user roles for the user
        List<UserRole> userRoles = UserRole.findAllByUser(getCurrentUser())

        //filter the roles where the role isn't general and has a data model and return the list
        List<DataModel> dataModels = []
        dataModels = userRoles.findResults{it.dataModel}
        return dataModels
    }


    //get all the roles that the user has for a model i.e. are they just a user or also an admin
    //this is used by the json marshaller and which passes the info to
    // the front end angular interface
    Set getRoles(String dataModelId){

        DataModel dataModel = DataModel.get(dataModelId)

        //if there is a data model then return the roles for that data model
        if(dataModel){
            Set<UserRole> userRoles = UserRole.findAllByUserAndDataModel(getCurrentUser(), dataModel)
            return userRoles.collect{it.role.authority}
        }

        //if not just return the general roles
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
