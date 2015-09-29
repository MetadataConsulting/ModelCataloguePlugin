package org.modelcatalogue.core.security

class UserAdminController extends grails.plugin.springsecurity.ui.UserController {

    def create(){
        super.create()
    }

    def save(){
        params.name = params.name ?: params.username
        super.save()
    }
}
