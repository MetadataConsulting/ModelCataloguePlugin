package org.modelcatalogue.core.security

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.authentication.dao.NullSaltSource
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.DataModel

class UserAdminController extends grails.plugin.springsecurity.ui.UserController {

    def modelCatalogueSecurityService

    def create(){
        super.create()
    }

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

    def update() {
        // we need to check uniqueness of email separately as it would be hard to change User class
        if (params.email) {
            def user = User.findByEmailAndIdNotEqual(params.email, params.id)
            if (user) {
                user.errors.rejectValue("email", "default.unique.message",
                    ["email", User.class, params.email] as Object[], "Email value needs to be unique.")
                render view: 'edit', model: buildUserModel(user)
                return
            }
        }

        params.name = params.name ?: params.username

        String passwordFieldName = SpringSecurityUtils.securityConfig.userLookup.passwordPropertyName

        def user = findById()
        if (!user) return
        if (!versionCheck('user.label', 'User', user, [user: user])) {
            return
        }

        def oldPassword = user."$passwordFieldName"


        user.properties = params


        if (params.password && !params.password.equals(oldPassword)) {
            String salt = saltSource instanceof NullSaltSource ? null : params.username
            user."$passwordFieldName" = springSecurityUiService.encodePassword(params.password, salt)
        }

        if (!user.save(flush: true)) {
            render view: 'edit', model: buildUserModel(user)
            return
        }

        String usernameFieldName = SpringSecurityUtils.securityConfig.userLookup.usernamePropertyName

        lookupUserRoleClass().removeAll user
        addRoles user
        userCache.removeUserFromCache user[usernameFieldName]
        flash.message = "${message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), user.id])}"
        redirect action: 'edit', id: user.id
    }

    def edit() {
        String usernameFieldName = SpringSecurityUtils.securityConfig.userLookup.usernamePropertyName

        def user = params.username ? lookupUserClass().findWhere((usernameFieldName): params.username) : null
        if (!user) user = findById()
        if (!user) return

        return buildUserRoleModel(user)
    }

    protected Map buildUserRoleModel(user) {

        List<String> roles = sortedRoles()

        def allRoles = [:]

        for (role in roles) {
            String authority = role
            allRoles[(role)] = false
            UserRole.findAllByUserAndRole(user, role).each{ UserRole userRole ->
                if(!userRole.dataModel){
                    allRoles[(role)] = true
                }
            }

        }

        //TODO: need to put this into the security service method
        List<UserRole> userRoles = UserRole.findAllByUser(user)

        //user roles map - data model is the key with a list of roles applicable to that model
        Map userRolesMap = [:]
        Map defaultRoleListForDataModel = [:]
        roles.each{ role ->
            defaultRoleListForDataModel.put(role, false)
        }

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

        return [user: user, roleMap: allRoles, userRoles: userRolesMap]

    }


    protected void addRoles(user) {
        String upperAuthorityFieldName = GrailsNameUtils.getClassName(SpringSecurityUtils.securityConfig.authority.nameField, null)

        for (String key in params.keySet()) {
            if (key.contains('ROLE') && 'on' == params.get(key)) {
                def splitkey = key.split("-", 2)
                if (splitkey.size() == 2) {
                    String dataModelId = splitkey[0]
                    String role = splitkey[1]
                    DataModel dataModel = DataModel.get(dataModelId.toLong())
                    modelCatalogueSecurityService.addUserRoleModel(user, lookupRoleClass()."findBy$upperAuthorityFieldName"(role), dataModel, true)
                } else {
                    lookupUserRoleClass().create user, lookupRoleClass()."findBy$upperAuthorityFieldName"(splitkey[0]), true
                }
            }
        }
    }

}
