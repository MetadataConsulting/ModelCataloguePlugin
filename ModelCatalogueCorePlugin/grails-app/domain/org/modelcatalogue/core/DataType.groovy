package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.publishing.PublishingChain
import org.modelcatalogue.core.publishing.PublishingContext
import org.modelcatalogue.core.util.DataTypeRuleScript
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.SecuredRuleExecutor

/*
* A Data Type is like a primitive type
* i.e. integer, string, byte, boolean, time........
* additional types can be specified (as well as enumerated types (see EnumeratedType))
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

    static transients = ['relatedDataElements', 'regexDef']

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

    void afterDraftPersisted(CatalogueElement draft, PublishingContext context) {
        super.afterDraftPersisted(draft, context)
        if (draft.instanceOf(DataType)) {
            for (DataElement de in getRelatedDataElements()) {
                if (de.status == ElementStatus.DRAFT) {
                    de.dataType = draft
                    FriendlyErrors.failFriendlySave(de)
                }
            }
        }
    }

    @Override
    protected PublishingChain prepareDraftChain(PublishingChain chain) {
        super.prepareDraftChain(chain).add(relatedDataElements)
    }

    @Override
    protected String getModelCatalogueResourceName() {
        'dataType'
    }

    Integer countVersions() {
        if (!latestVersionId) {
            return 1
        }
        DataType.countByLatestVersionId(latestVersionId)
    }
}
