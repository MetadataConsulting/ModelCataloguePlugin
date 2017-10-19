package org.modelcatalogue.core.security.ss2x

import grails.plugin.springsecurity.userdetails.GrailsUser
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.security.UserGormService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

class ApiKeyDaoAuthenticationProvider extends DaoAuthenticationProvider {

    UserGormService userGormService

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (!(userDetails instanceof GrailsUser)) {
            super.additionalAuthenticationChecks(userDetails, authentication)
        }

        try {
            super.additionalAuthenticationChecks(userDetails, authentication);
        } catch (BadCredentialsException bce) {
            User user = userGormService.findByUsername(userDetails.username)

            if (!user) {
                throw new UsernameNotFoundException("User $userDetails.username not found")
            }

            String presentedPassword = authentication.credentials

            if (user.apiKey != presentedPassword) {
                logger.debug("Authentication failed: api key does not match stored value");
                throw bce;
            }
        }
    }
}
