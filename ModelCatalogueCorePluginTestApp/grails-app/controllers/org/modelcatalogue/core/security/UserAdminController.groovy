package org.modelcatalogue.core.security

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.authentication.dao.NullSaltSource
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.DataModel

class UserAdminController extends grails.plugin.springsecurity.ui.UserController {

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

        return buildUserRoleModel(user)
    }

    /**
     * update a users details
     * @params email, email of user
     * @params name, username - name of the user
     */

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

    //TODO: this is a bit longwinded

    protected Map buildUserRoleModel(user) {

        //get a list of the roles available and put them in a sorted list
        List<String> roles = sortedRoles()

        //all the general roles with a true, false status if the user has them
        def generalRoles = [:]

        //for each of the available roles add a role status
        //only add if this is a general role i.e. it isn't specific to a data model
        //specific roles come later
        for (role in roles) {
            generalRoles[(role)] = false
            UserRole.findAllByUserAndRole(user, role).each{ UserRole userRole ->
                if(!userRole.dataModel){
                    generalRoles[(role)] = true
                }
            }

        }

        def specificRoles = [Role.findByAuthority("ROLE_METADATA_CURATOR"), Role.findByAuthority("ROLE_USER")]

        //TODO: need to put this into the security service method
        //TODO: could create a groovy class to store this instead of HASHMAPS
        // get a list of all the roles that the user has
        List<UserRole> userRoles = UserRole.findAllByUser(user)

        // create a user roles model map
        // data model id is the key of the map
        // the data model key returns a map with two properties:
        // a data model name
        // and a with a list of roles applicable to that model
        // i.e. ["25: [name: "data model with id of 25", roleList: [METADATA_CURATOR: false, USER_ROLE: true]], 28: [name: ........]
        Map userRolesMap = [:]

        //create a default list of roles that are applicable to each model,
        Map defaultRoleListForDataModel = [:]
        specificRoles.each{ role ->
            // set as false i.e. the user doesn't have a role assigned to a model by default
            defaultRoleListForDataModel.put(role, false)
        }

        //iterate through each of the assigned roles for the user adding them to the map
        userRoles.each{ UserRole userRole ->
            //create a map of the data model properties to populate for each data model : includes name and roleList
            Map<String, Map> dataModelProperties = [:]

            //create role list based on default role list
            Map<DataModel, Map> roleListForDataModel = [:]
            roleListForDataModel.putAll(defaultRoleListForDataModel)

            //find the data model in the userRole, if there is one
            DataModel model = userRole?.dataModel

            //create string name based on the data model name
            String dataModelName =  "${model?.name} (${model?.defaultModelCatalogueId})"

            //populate the data model properties with name and role list
            dataModelProperties.put("name", dataModelName)

            //if there is a model and the current userRoles map copy the data model properties
            if (model && userRolesMap.get(model.id)) {
                dataModelProperties = userRolesMap.get(model.id)
                roleListForDataModel.putAll(dataModelProperties.get("roleList"))
            }

            if(model){
                roleListForDataModel.put(userRole?.role, true)
                dataModelProperties.put("roleList", roleListForDataModel)
                userRolesMap.put(model.id, dataModelProperties)
            }
        }


        //add all the data models that haven't been associated with the user
        //this is to display in the ui so that the  role can be added to a specific model for the user
        List<DataModel> dataModels = DataModel.list()
        dataModels.each{ model ->
            if(!userRolesMap.get(model.id)) {
                //create a map of the data model properties to populate for each data model : includes name and roleList
                Map<String, Map> dataModelProperties = [:]

                //create string name based on the data model name
                String dataModelName = "${model?.name} (${model?.defaultModelCatalogueId}"

                //populate the data model properties with name and role list
                dataModelProperties.put("name", dataModelName)

                if (model) {
                    dataModelProperties.put("roleList", defaultRoleListForDataModel)
                    userRolesMap.put(model.id, dataModelProperties)
                }
            }
        }

        return [user: user, generalRoles: generalRoles, specificRoles: specificRoles, userRoles: userRolesMap]

    }


    /**
     * method to add general role OR a model specific role for a user
     * @params any param that contains ROLE and return 'on' is added but doesn't have an ID is a general role and is added
     * @params any param that contains ROLE and return 'on' and has a prefix with an id is a model specific role, the prefix before the - is the model id, and the suffix is the role to be added
     */
    protected void addRoles(user) {
        //get the Role class - if it's named something else - which it isn't
        String upperAuthorityFieldName = GrailsNameUtils.getClassName(SpringSecurityUtils.securityConfig.authority.nameField, null)

        //fof all the parameters identify the ones that contain ROLE
        for (String key in params.keySet()) {
            if (key.contains('ROLE') && 'on' == params.get(key)) {
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
