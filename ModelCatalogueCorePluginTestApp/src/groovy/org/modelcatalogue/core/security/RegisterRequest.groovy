package org.modelcatalogue.core.security

interface RegisterRequest {

    String getUsername()
    String getEmail()
    String getPassword()
}