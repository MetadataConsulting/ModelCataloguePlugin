package org.modelcatalogue.core.search

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(KeywordsService)
class KeywordsServiceSpec extends Specification {

    @Unroll()
    def 'stem #term into #expected'(String term, String expected) {
        expect:
        expected == service.stemTerm(term)

        where:
        term        || expected
        'PHA'       || 'pha'
        'controler' || 'control'
        'Background'|| 'background'
        'Stimulated'|| 'stimul'
    }


    @Unroll()
    def '#term cleanup => #expected'(String term, String expected) {
        expect:
        expected == service.cleanup(term)

        where:
        term                           || expected
        'Blood lactate/pyruvate ratio' || 'Blood lactate pyruvate ratio'
    }

    @Unroll()
    def '#term keywords => #keywords'(String term, List<String> keywords) {
        expect:
        keywords == service.keywords(term, true)

        where:
        term                         || keywords
        'PHA Stimulated Lymphocytes' || ['pha', 'stimul', 'lymphocyt']
        '% of CD19+ B Cells'         || ['cd19+', 'cell']
    }
}
