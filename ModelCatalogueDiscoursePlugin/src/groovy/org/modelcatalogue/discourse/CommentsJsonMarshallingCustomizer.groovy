package org.modelcatalogue.discourse

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.util.marshalling.JsonMarshallingCustomizer
import org.springframework.beans.factory.annotation.Autowired

class CommentsJsonMarshallingCustomizer extends JsonMarshallingCustomizer {

    @Autowired DiscourseService discourseService

    def customize(el, json) {
        if (!discourseService.discourseServerUrl) return json
        def result = json ?: [:]
        if (el instanceof CatalogueElement) {
            result.comments = [
                    count: 0,
                    itemType: 'org.discourse.api.Post',
                    link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/comments"
            ]
        }
        return result
    }

}
