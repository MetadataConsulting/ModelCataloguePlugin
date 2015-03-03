package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.publishing.PublishingChain
import org.modelcatalogue.core.util.FriendlyErrors

/*
* A Data Type is like a primitive type
* i.e. integer, string, byte, boolean, time........
* additional types can be specified (as well as enumerated types (see EnumeratedType))
* Data Types are used by Value Domains (please see ValueDomain and Usance)
*/


class DataType extends CatalogueElement {

    static constraints = {
        name size: 1..255
    }

    static mapping = {
        tablePerHierarchy false
    }

    static transients = ['relatedValueDomains']

    static String suggestName(Set<String> suggestions) {
        if (!suggestions) {
            return null
        }
        if (suggestions.size() == 1) {
            return suggestions[0]
        }

        List<List<String>> words = suggestions.collect { GrailsNameUtils.getNaturalName(it).split(/\s/).toList() }

        List<String> result = words.head()

        for (List<String> others in words.tail()) {
            result = result.intersect(others)
        }

        result.join(" ")
    }

    List<ValueDomain> getRelatedValueDomains() {
        if (!readyForQueries) {
            return []
        }
        return ValueDomain.findAllByDataType(this)
    }

    Long countRelatedValueDomains() {
        if (!readyForQueries) {
            return 0
        }
        return ValueDomain.countByDataType(this)
    }

    DataType removeFromRelatedValueDomains(ValueDomain domain) {
        domain.dataType = null
        FriendlyErrors.failFriendlySave(domain)
        this
    }

    @Override
    protected PublishingChain prepareDraftChain(PublishingChain chain) {
        chain.add(this.relatedValueDomains).add(this.classifications)
    }
}
