package org.modelcatalogue.core.security

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.persistence.UserAuthenticationGormService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.event.AbstractAuthenticationEvent
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent
import org.springframework.security.authentication.event.AuthenticationFailureCredentialsExpiredEvent
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent
import org.springframework.security.authentication.event.AuthenticationFailureExpiredEvent
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent
import org.springframework.security.authentication.event.AuthenticationFailureProviderNotFoundEvent
import org.springframework.security.authentication.event.AuthenticationFailureServiceExceptionEvent
import org.springframework.security.authentication.event.AuthenticationSuccessEvent

@Slf4j
@CompileStatic
class MetadataSecurityEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    UserAuthenticationGormService userAuthenticationGormService

    @CompileDynamic
    private String usernameByEvent(AbstractAuthenticationEvent event) {
        def principal = event.authentication?.principal
        if ( principal instanceof String) {
            return principal
        }
        if ( principal.hasProperty('username') ) {
            return principal.username
        }
        ''
    }

    @Override
    void onApplicationEvent(AuthenticationSuccessEvent event) {
        switch (event) {
            case AuthenticationSuccessEvent:
                String username = usernameByEvent(event)
                if ( !username ) {
                    log.warn 'unable to extract username from AuthenticationSuccessEvent'
                }
                switch(event.source) {
                    case UsernamePasswordAuthenticationToken:
                        log.debug '{} authenticated successfully with username / password authentication', username
                        userAuthenticationGormService.save(username)
                        break
                    default:
                        log.debug '{} authenticated successfully', username
                }
                break
            case AuthenticationFailureExpiredEvent:
                log.debug 'AuthenticationFailureExpiredEvent {}', usernameByEvent(event)
                break

            case AuthenticationServiceException:
                log.debug 'AuthenticationServiceException {}', usernameByEvent(event)
                break

            case AuthenticationFailureServiceExceptionEvent:
                log.debug 'AuthenticationFailureServiceExceptionEvent {}', usernameByEvent(event)
                break

            case AuthenticationFailureLockedEvent:
                log.debug 'AuthenticationFailureLockedEvent {}', usernameByEvent(event)
                break

            case AuthenticationFailureCredentialsExpiredEvent:
                log.debug 'AuthenticationFailureCredentialsExpiredEvent {}', usernameByEvent(event)
                break

            case AuthenticationFailureDisabledEvent:
                log.debug 'AuthenticationFailureDisabledEvent {}', usernameByEvent(event)
                break

            case AuthenticationFailureBadCredentialsEvent:
                log.debug '{} authentication failed with bad credentials', usernameByEvent(event)
                break

            case AuthenticationFailureProviderNotFoundEvent:
                log.debug 'AuthenticationFailureProviderNotFoundEvent {}', usernameByEvent(event)
                break
        }
    }
}
