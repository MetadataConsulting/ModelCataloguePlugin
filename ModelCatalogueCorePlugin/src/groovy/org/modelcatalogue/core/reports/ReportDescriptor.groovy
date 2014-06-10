package org.modelcatalogue.core.reports

import org.codehaus.groovy.grails.web.mapping.LinkGenerator

/**
 * Created by ladin on 09.06.14.
 */
class ReportDescriptor {

    LinkGenerator generator

    /**
     * Title of the report to be displayed in the front end.
     */
    String title

    /**
     * List of conditions which all needs to be met to apply make this report available to given endpoint.
     */
    List<Closure> conditions = []

    /**
     * Additional params to be used to generate the destination URL.
     *
     * Use <code>true</code> instead of actual <code>id</code> parameter if <code>id</code> parameter is required.
     *
     * @see org.codehaus.groovy.grails.web.mapping.LinkGenerator
     */
    Closure<Map> linkParams = {[:]}

    boolean appliesTo(Object model) {
        for (condition in conditions) {
            if (!condition(model)) return false
        }
        return true
    }

    String getLink(Object model) {
        Map params = new HashMap(linkParams(model))
        if (params.id) {
            if (model.hasProperty('id')) {
                params.id = model.id
            } else {
                params.remove('id')
            }

        }

        if(!params.method) {
            params.method = 'GET'
        }

        generator.link(params)
    }


}
