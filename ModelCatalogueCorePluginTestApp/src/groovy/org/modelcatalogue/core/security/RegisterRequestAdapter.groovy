package org.modelcatalogue.core.security

import grails.plugin.springsecurity.ui.RegisterCommand

class RegisterRequestAdapter implements RegisterRequest {
    RegisterCommand command
    RegisterRequestAdapter(RegisterCommand command) {
        this.command = command
    }

    @Override
    String getUsername() {
        return command.username
    }

    @Override
    String getEmail() {
        return command.email
    }

    @Override
    String getPassword() {
        return command.password
    }
}
