package org.modelcatalogue.core.security

class UserAdminController extends grails.plugin.springsecurity.ui.UserController {

    /**
     * show the create user page - see supercalss
     */
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
        super.update()
    }
}
