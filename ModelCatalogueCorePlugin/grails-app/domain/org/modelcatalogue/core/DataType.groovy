package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.publishing.PublishingChain
import org.modelcatalogue.core.util.DataTypeRuleScript
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.SecuredRuleExecutor

/*
* A Data Type is like a primitive type
* i.e. integer, string, byte, boolean, time........
* additional types can be specified (as well as enumerated types (see EnumeratedType))
* Data Types are used by Value Domains (please see ValueDomain and Usance)
*/


class DataType extends CatalogueElement {

    String rule

    static constraints = {
        name size: 1..255

        rule nullable:true, maxSize: 10000, validator: { val,obj ->
            if(!val){return true}
            SecuredRuleExecutor.ValidationResult result = new SecuredRuleExecutor(DataTypeRuleScript, new Binding(x: null, dataType: obj)).validate(val)
            result ? true : ['wontCompile', result.compilationFailedMessage]
        }
    }

    void setRegexDef(String regex) {
        if (!regex) {
            rule = null
        } else {
            rule = "x ==~ /$regex/"
        }
    }

    String getRegexDef() {
        def match = rule =~ "x ==~ /(.+)/"
        if (match) {
            return match[0][1]
        }
        null
    }

    boolean isEnumKey(Object x) {
        return true
    }

    /**
     * Validates given value. Only boolean value true is considered as valid.
     *
     * As falsy method you can for example return boolean null, false, any String or any Exception.
     *
     * @param x
     * @return
     */
    def validateRule(Object x) {
        if (!isEnumKey(x)) {
            return false
        }

        if (hasProperty('isBasedOn')) {
            for (DataType domain in isBasedOn) {
                def result = domain.validateRule(x)
                if (result != null && (!(result instanceof Boolean) || result.is(false))) {
                    return result
                }
            }
        }

        if (rule) {
            return new SecuredRuleExecutor(DataTypeRuleScript, new Binding(x: x, dataType: this)).execute(rule)
        }
        return true
    }

    static mapping = {
        tablePerHierarchy false
    }

    static transients = ['relatedValueDomains', 'relatedDataElements', 'regexDef']

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

    @Deprecated
    List<ValueDomain> getRelatedValueDomains() {
        if (!readyForQueries) {
            return []
        }
        return ValueDomain.findAllByDataType(this)
    }

    @Deprecated
    Long countRelatedValueDomains() {
        if (!readyForQueries) {
            return 0
        }
        return ValueDomain.countByDataType(this)
    }

    @Deprecated
    DataType removeFromRelatedValueDomains(ValueDomain domain) {
        domain.dataType = null
        FriendlyErrors.failFriendlySave(domain)
        this
    }

    List<DataElement> getRelatedDataElements() {
        if (!readyForQueries) {
            return []
        }
        if (archived) {
            return DataElement.findAllByDataType(this)
        }
        return DataElement.findAllByDataTypeAndStatusInList(this, [ElementStatus.FINALIZED, ElementStatus.DRAFT])
    }

    Long countRelatedDataElements() {
        if (!readyForQueries) {
            return 0
        }
        if (archived) {
            return DataElement.countByDataType(this)
        }
        return DataElement.countByDataTypeAndStatusInList(this, [ElementStatus.FINALIZED, ElementStatus.DRAFT])
    }

    DataType removeFromRelatedDataElements(DataElement element) {
        element.dataType = null
        FriendlyErrors.failFriendlySave(element)
        this
    }

    @Override
    void afterMerge(CatalogueElement destination) {
        if (!(destination.instanceOf(DataType))) {
            return
        }
        List<DataElement> dataElements = new ArrayList<DataElement>(relatedDataElements)
        for (DataElement element in dataElements) {
            element.dataType = destination as DataType
            FriendlyErrors.failFriendlySave(element)
        }
    }


    @Override
    protected PublishingChain prepareDraftChain(PublishingChain chain) {
        chain.add(this.relatedValueDomains).add(this.dataModels)
    }
}
