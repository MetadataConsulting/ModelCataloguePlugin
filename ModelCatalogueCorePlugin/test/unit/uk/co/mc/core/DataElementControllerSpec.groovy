package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.plugins.web.mimes.MimeTypesFactoryBean
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(DataElementController)
@Mock(DataElement)
class DataElementControllerSpec extends Specification {

    def setup() {
        def ga = grailsApplication
        ga.config.grails.mime.types =
                [ html: ['text/html','application/xhtml+xml'],
                        xml: ['text/xml', 'application/xml'],
                        text: 'text/plain',
                        js: 'text/javascript',
                        rss: 'application/rss+xml',
                        atom: 'application/atom+xml',
                        css: 'text/css',
                        csv: 'text/csv',
                        all: '*/*',
                        json: ['application/json','text/json'],
                        form: 'application/x-www-form-urlencoded',
                        multipartForm: 'multipart/form-data'
                ]

        defineBeans {
            mimeTypes(MimeTypesFactoryBean) {
                grailsApplication = ga
            }
        }

        new DataElement(id: 1, name: "One", description: "First data element", definition: "First data element definition").save()
        new DataElement(id: 2, name: "Two", description: "Second data element", definition: "Second data element definition").save()
        new DataElement(id: 3, name: "Three", description: "Third data element", definition: "Third data element definition").save()
    }

    def cleanup() {
    }

    void "Get list of data elements as JSON"() {
        expect:
        DataElement.count() == 3

        when:
        controller.list()

        def json = response.json

        then:
        json.success
        json.size           == 3
        json.total          == 3
        json.list
        json.list.size()    == 3
        json.list.any { it.id == 1 }
        json.list.any { it.id == 2 }
        json.list.any { it.id == 3 }

    }

    @Unroll
    void "Get list of data elements as JSON paged should have size #size and first element id #id for params #theParams"() {
        expect:
        DataElement.count() == 3

        when:
        theParams.each { key, val ->
            params[key] = val
        }


        controller.list()
        def json = response.json

        then:
        json.success
        json.size               == size
        json.total              == 3
        json.list
        json.list.first().id    == id


        where:
        size    | id | theParams
        2       | 2  | [offset: 1]
        2       | 1  | [max: 2]
        1       | 2  | [offset: 1, max: 1]

    }

    void "Get an element"()
    {
        expect:
         DataElement.count()==3


        when:
        controller.get(1)
        def result = response.json

        then:
        result.instance
        result.instance.id == 1
        result.instance.name == "One"

    }

    void "If element not found "()
    {

        expect:
        DataElement.count()==3


        when:
        controller.get(13)
        def result = response.json


        then:
        !result.instance
        result.errors

    }



}
