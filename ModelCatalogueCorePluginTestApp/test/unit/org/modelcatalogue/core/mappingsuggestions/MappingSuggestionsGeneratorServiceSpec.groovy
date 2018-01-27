package org.modelcatalogue.core.mappingsuggestions

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(MappingSuggestionsGeneratorService)
class MappingSuggestionsGeneratorServiceSpec extends Specification  {

    def "addToSuggestions keeps a max number of elements and ordered"() {

        when:
        def results = service.addToSuggestions([] as List<SourceDestinationMappingSuggestion>, null, null, 0.7f, 3)

        then:
        results.size() == 1
        results*.distance == [0.7f]

        when:
        results = service.addToSuggestions(results, null, null, 0.8f, 3)

        then:
        results*.distance == [0.8f, 0.7f]

        when:
        results = service.addToSuggestions(results, null, null, 0.65f, 3)

        then:
        results*.distance == [0.8f, 0.7f, 0.65f]

        when:
        results = service.addToSuggestions(results, null, null, 0.75f, 3)

        then:
        results*.distance == [0.8f, 0.75f, 0.7f]

        when:
        results = service.addToSuggestions(results, null, null, 0.95f, 3)

        then:
        results*.distance == [0.95f, 0.8f, 0.75f]

    }

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
        keywords == service.keywords(term)

        where:
        term                         || keywords
        'PHA Stimulated Lymphocytes' || ['pha', 'stimul', 'lymphocyt']
        '% of CD19+ B Cells'         || ['cd19+', 'cell']
    }
}

