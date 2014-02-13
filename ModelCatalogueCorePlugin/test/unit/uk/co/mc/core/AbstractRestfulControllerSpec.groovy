package uk.co.mc.core

import org.codehaus.groovy.grails.plugins.web.mimes.MimeTypesFactoryBean
import spock.lang.Specification

/**
 * Created by ladin on 13.02.14.
 */
abstract class AbstractRestfulControllerSpec extends Specification {

    def setup() {
        setupMimeTypes()
    }

    protected void setupMimeTypes() {
        def ga = grailsApplication
        ga.config.grails.mime.types =
                [html: ['text/html', 'application/xhtml+xml'],
                        xml: ['text/xml', 'application/xml'],
                        text: 'text/plain',
                        js: 'text/javascript',
                        rss: 'application/rss+xml',
                        atom: 'application/atom+xml',
                        css: 'text/css',
                        csv: 'text/csv',
                        all: '*/*',
                        json: ['application/json', 'text/json'],
                        form: 'application/x-www-form-urlencoded',
                        multipartForm: 'multipart/form-data'
                ]

        defineBeans {
            mimeTypes(MimeTypesFactoryBean) {
                grailsApplication = ga
            }
        }
    }

}
