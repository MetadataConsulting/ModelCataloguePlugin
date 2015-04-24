package org.modelcatalogue.discourse

import grails.converters.JSON
import org.modelcatalogue.core.SecurityService
import org.springframework.http.HttpStatus

class CommentsController {

    DiscourseService discourseService
    SecurityService modelCatalogueSecurityService

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



            topic = discourseService.discourse.topics.getTopic(topicId)
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



    private void forbidden() {
        render status: HttpStatus.FORBIDDEN
    }

    private void notFound() {
        render status: HttpStatus.NOT_FOUND
    }

}
