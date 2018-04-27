package org.modelcatalogue.core.search

import groovy.transform.CompileStatic
import org.modelcatalogue.core.mappingsuggestions.Stemmer

@CompileStatic
class KeywordsService {

    String stemTerm (String term) {
        if ( !term ) {
            return term
        }
        term = term.toLowerCase()
        Stemmer stemmer = new Stemmer()
        term.toCharArray().each { char c ->
            stemmer.add(c)
        }
        stemmer.stem()
        stemmer.toString()
    }

    String cleanup(String term) {
        if ( !term ) {
            return term
        }

        term.replaceAll('/', ' ')
                .replaceAll('\\(', ' ')
                .replaceAll(']', ' ')
                .replaceAll('\\[', ' ')
                .trim().replaceAll(" +", " ");
    }

    List<String> keywords(String term, boolean applyWordStem = false) {
        String[] arr = cleanup(term).split(' ')
        List<String> result = []
        for ( String word : arr ) {
            result.add(applyWordStem ? stemTerm(word) : word)
        }
        result.findAll { !EnglishGrammar.ALL.contains(it) && it.length() > 1 }
    }
}
