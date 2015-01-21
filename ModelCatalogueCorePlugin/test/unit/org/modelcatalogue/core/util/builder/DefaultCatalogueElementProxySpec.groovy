package org.modelcatalogue.core.util.builder

import spock.lang.Specification

class DefaultCatalogueElementProxySpec extends Specification {


    def "Match normalized strings"() {
        expect:
        DefaultCatalogueElementProxy.normalizeWhitespace("""
                ONE
                TWO
                THREE
        """) == DefaultCatalogueElementProxy.normalizeWhitespace("""

                ONE
                TWO
                THREE
        """)
    }

}
