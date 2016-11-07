import grails.plugin.springsecurity.SpringSecurityUtils

class SpringOAuthAjaxFixFilters {

    def filters = {
        backToDashboardAfterLink(controller:'springSecurityOAuth', action:'(askToLinkOrCreateAccount)|(onSuccess)') {
            before = {
                // we don't want any redirect back
                session.removeAttribute(SpringSecurityUtils.SAVED_REQUEST)
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }
}
