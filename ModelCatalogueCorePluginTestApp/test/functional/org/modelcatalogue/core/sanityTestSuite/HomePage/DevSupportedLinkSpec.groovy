package org.modelcatalogue.core.sanityTestSuite.HomePage

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.HomePage
import spock.lang.Ignore

//@IgnoreIf({ !System.getProperty('geb.env')  })
@Ignore
class DevSupportedLinkSpec extends GebSpec {

    void modelDevelopmentLinks() {
        when:
        HomePage homePage = to HomePage

        then:
        at HomePage

        and:
        homePage.footer.logoLinks.size() == 4

        and: 'image alt attribute is set'
        homePage.footer.logoLinksAlt.size() == 4

        and: 'links open in different window / tag'
        homePage.footer.linkImages.each {
            assert it.parent('a').getAttribute('target') == '_blank'
        }
    }
}

