package org.modelcatalogue.core

import grails.test.mixin.TestFor
import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(MDXFeaturesService)
class MDXFeaturesServiceSpec extends IntegrationSpec {

    GrailsApplication grailsApplication


    void "test when config values are 'true'"() {
        when:
        grailsApplication.config.mdx.features.northThames = 'true'
        grailsApplication.config.mdx.features.gel = 'true'
        MDXFeatures mdxFeatures = service.getMDXFeatures()

        then:
        assert mdxFeatures.northThamesFeatures
        assert mdxFeatures.gelFeatures
    }

    void "test when values are not 'true'"() {
        when:
        grailsApplication.config.mdx.features.northThames = 'xyz'
        grailsApplication.config.mdx.features.gel = null
        MDXFeatures mdxFeatures = service.getMDXFeatures()

        then:
        assert !mdxFeatures.northThamesFeatures
        assert !mdxFeatures.gelFeatures
    }
}
