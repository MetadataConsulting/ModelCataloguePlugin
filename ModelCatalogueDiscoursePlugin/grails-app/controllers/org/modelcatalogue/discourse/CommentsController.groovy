package org.modelcatalogue.discourse

import grails.converters.JSON
import org.modelcatalogue.core.SecurityService
import org.modelcatalogue.discourse.sso.SingleSignOn
import org.springframework.http.HttpStatus

class CommentsController {

    DiscourseService discourseService
    SecurityService modelCatalogueSecurityService

    def discourseUser() {
        if (!modelCatalogueSecurityService.userLoggedIn) {
            return forbidden()
        }
        String username = discourseService.ensureUserExistsInDiscourse(modelCatalogueSecurityService.currentUser)
        render discourseService.getDiscourse(username).users.getUser(username, [:]).data as JSON
    }

    def createComment() {
        if (!modelCatalogueSecurityService.userLoggedIn) {
            return forbidden()
        }

        Long id = params.long('id')
        if (!id) {
            return notFound()
        }


        String raw = request.JSON.raw

        render discourseService.getDiscourse(modelCatalogueSecurityService.currentUser.username).posts.createPost(discourseService.findOrCreateDiscourseTopic(id), raw).data as JSON

    }

    def comments() {
        if (!modelCatalogueSecurityService.userLoggedIn) {
            return forbidden()
        }

        Long id = params.long('id')
        if (!id) {
            return notFound()
        }
        def topic
        try {
            Long topicId = discourseService.findOrCreateDiscourseTopic(id)
            if (!topicId) {
                return notFound()
            }

            log.info "Discourse Topic ID [$topicId] found for Catalogue Element [$id]"



            topic = discourseService.getDiscourse(modelCatalogueSecurityService.currentUser.username).topics.getTopic(topicId)
        } catch (e) {
            render([errors: [message: e.message]] as JSON)
            return
        }

        if (!topic || topic.status != 200) {
            render([errors: [message: "Discourse Error"]] as JSON)
            return
        }

        render topic.data as JSON
    }

    def sso() {
        if (!modelCatalogueSecurityService.userLoggedIn) {
            return forbidden()
        }

        SingleSignOn singleSignOn = discourseService.discourse.singleSignOn
        redirect url: singleSignOn.getRedirectURL(singleSignOn.verifyParameters(params.sso as String, params.sig as String), discourseService.discourseUser)
    }



    private void forbidden() {
        render status: HttpStatus.FORBIDDEN
    }

    private void notFound() {
        render status: HttpStatus.NOT_FOUND
    }

}
