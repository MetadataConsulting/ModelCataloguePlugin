package org.modelcatalogue.core.security

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.authentication.dao.NullSaltSource
import grails.transaction.Transactional
import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService

class UserAdminController extends grails.plugin.springsecurity.ui.UserController {

    RoleService roleService

    UserRoleService userRoleService

    DataModelService dataModelService

    def modelCatalogueSecurityService

    /**
     * show the create user page - see supercalss
     */
    def create(){
        super.create()
    }


    /**
     * save changes to a user - see superclass
     * simple user creation - add roles later using the edit stuff
     * @params email, email of user
     * @params name, useranem - name of the user
     */
    def save(){
        // we need to check uniqueness of email separately as it would be hard to change User class
        if (params.email) {
            def user = User.findByEmail(params.email)
            if (user) {
                user.errors.rejectValue("email", "default.unique.message",
                    ["email", User.class, params.email] as Object[], "Email value needs to be unique.")
                render view: 'create', model: [user: user, authorityList: sortedRoles()]
                return
            }
        }

        params.name = params.name ?: params.username
        super.save()
    }


    /**
     * show the edit userpage and the different user roles associated with various models
     * @params user id
     */
    def edit() {
        String usernameFieldName = SpringSecurityUtils.securityConfig.userLookup.usernamePropertyName

        def user = params.username ? lookupUserClass().findWhere((usernameFieldName): params.username) : null
        if (!user) user = findById()
        if (!user) return

        buildUserRoleModel(user)
    }

    /**
     * update a users details
     * @params email, email of user
     * @params name, username - name of the user
     */
    @Transactional
    def update() {

        // we need to check uniqueness of email separately as it would be hard to change User class
        if (params.email) {
            def user = User.findByEmailAndIdNotEqual(params.email, params.id)
            if (user) {
                user.errors.rejectValue("email", "default.unique.message",
                    ["email", User.class, params.email] as Object[], "Email value needs to be unique.")
                render view: 'edit', model: buildUserRoleModel(user)
                return
            }
        }

        // get the username
        params.name = params.name ?: params.username

        // get password field name
        String passwordFieldName = SpringSecurityUtils.securityConfig.userLookup.passwordPropertyName

        // get the user
        def user = findById()
        if (!user) return
        if (!versionCheck('user.label', 'User', user, [user: user])) {
            return
        }

        def oldPassword = user."$passwordFieldName"

        // populate user properties based on the params
        user.properties = params


        // ensure that the password isn't the same as the old password
        if (params.password && !params.password.equals(oldPassword)) {
            String salt = saltSource instanceof NullSaltSource ? null : params.username
            user."$passwordFieldName" = springSecurityUiService.encodePassword(params.password, salt)
        }


        // try and save the user if you can or go back to edit screen
        if (!user.save(flush: true)) {
            render view: 'edit', model: buildUserRoleModel(user)
            return
        }

        String usernameFieldName = SpringSecurityUtils.securityConfig.userLookup.usernamePropertyName

        //remove all the old user roles
        lookupUserRoleClass().removeAll user

        //add all the user roles - general roles and model specific roles
        addRoles user


        userCache.removeUserFromCache user[usernameFieldName]
        flash.message = "${message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), user.id])}"
        redirect action: 'edit', id: user.id
    }


    /**
     * return a map of the general user roles and the model specific roles
     * @params user, the user you want to display roles for
     */
    @CompileStatic
    protected Map buildUserRoleModel(User user) {
        [
                roleList: sortedRoles(),
                user: user,
                userRoleOverview: UserRoleOverview.of(userRoleService.findAllByUser(user)),
                specificRolesList: roleService.findAllSpecificRoles(),
                dataModelList: dataModelService.findAll()
        ]
    }

    /**
     * method to add general role OR a model specific role for a user
     * @params any param that contains ROLE and return 'on' is added but doesn't have an ID is a general role and is added
     * @params any param that contains ROLE and return 'on' and has a prefix with an id is a model specific role, the prefix before the - is the model id, and the suffix is the role to be added
     */
    protected void addRoles(user) {
        //get the Role class - if it's named something else - which it isn't
        String upperAuthorityFieldName = GrailsNameUtils.getClassName(SpringSecurityUtils.securityConfig.authority.nameField, null)

        List<String> roles = params.list('roles')
        if ( roles ) {
            for (String key in roles) {
                //split the param to see if it is a general role or a model specific role
                def splitkey = key.split("-", 2)
                //if it has two components it's a model specific role
                if (splitkey.size() == 2) {
                    String dataModelId = splitkey[0]
                    String role = splitkey[1]
                    DataModel dataModel = DataModel.get(dataModelId.toLong())
                    modelCatalogueSecurityService.addUserRoleModel(user, lookupRoleClass()."findBy$upperAuthorityFieldName"(role), dataModel, true)
                } else {
                    //it's a general role
                    lookupUserRoleClass().create user, lookupRoleClass()."findBy$upperAuthorityFieldName"(splitkey[0]), true
                }
            }
        }
    }


}
