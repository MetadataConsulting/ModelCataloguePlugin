package org.modelcatalogue.core

import org.modelcatalogue.core.publishing.Publisher
import org.modelcatalogue.core.publishing.PublishingChain
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.SecuredRuleExecutor
import org.modelcatalogue.core.util.ValueDomainRuleScript

/*
* subjects, isbn, rating
*
* ValueDomain subjects with EnumeratedType enumerations['history', 'science', 'politics'],
* ValueDomain isbn with regex "^\d{9}[\d|X]$" and DataType String,
* ValueDomain rating with regex "\b[0-5]\b" and DataType Integer,
* ValueDomain content with regex "^[:;,\-@0-9a-zA-Zâéè'.\s]{1,2000000}$"  and DataType String,
*
*  *  * 	<xs:simpleType name="SACTDrugRouteOfAdminType">
		<xs:restriction base="CodeListType">
			<xs:enumeration value="01"/>
			<xs:enumeration value="02"/>
			<xs:enumeration value="03"/>
			<xs:enumeration value="04"/>
			<xs:enumeration value="05"/>
			<xs:enumeration value="06"/>
			<xs:enumeration value="07"/>
			<xs:enumeration value="08"/>
			<xs:enumeration value="09"/>
			<xs:enumeration value="10"/>
			<xs:enumeration value="11"/>
			<xs:enumeration value="12"/>
			<xs:enumeration value="99"/>
		</xs:restriction>
	</xs:simpleType>
 *
 * !!!!!!!!VALUE DOMAINS Need to be related to at least one conceptual domain.....we need to build this into the
 * constraints
 *
 *
*/

class ValueDomain extends CatalogueElement {

    DataType dataType
    MeasurementUnit unitOfMeasure

	String rule
    Boolean multiple = Boolean.FALSE

    static transients = ['regexDef', 'dataElements']

    static constraints = {
        description nullable: true, maxSize: 2000
        unitOfMeasure nullable: true
        dataType        nullable: true

		rule nullable:true, maxSize: 10000, validator: { val,obj ->
            if(!val){return true}
            SecuredRuleExecutor.ValidationResult result = new SecuredRuleExecutor(ValueDomainRuleScript, new Binding(x: null, domain: obj)).validate(val)
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
        if (!dataType?.instanceOf(EnumeratedType)) {
            return true
        }
        if (!x) {
            return true
        }
        Set<String> enums = new HashSet<String>(dataType.enumerations.keySet())
        if (!enums.contains(x.toString())) {
            return false
        }
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
            for (ValueDomain domain in isBasedOn) {
                def result = domain.validateRule(x)
                if (result != null && (!(result instanceof Boolean) || result.is(false))) {
                    return result
                }
            }
        }

        if (rule) {
            return new SecuredRuleExecutor(ValueDomainRuleScript, new Binding(x: x, domain: this)).execute(rule)
        }
        return true
    }


    List<DataElement> getDataElements() {
        if (!readyForQueries) {
            return []
        }
        if (archived) {
            return DataElement.findAllByValueDomain(this)
        }
        return DataElement.findAllByValueDomainAndStatusInList(this, [ElementStatus.FINALIZED, ElementStatus.DRAFT])
    }

    Long countDataElements() {
        if (!readyForQueries) {
            return 0
        }
        if (archived) {
            return DataElement.countByValueDomain(this)
        }
        return DataElement.countByValueDomainAndStatusInList(this, [ElementStatus.FINALIZED, ElementStatus.DRAFT])
    }

    @Override
    CatalogueElement publish(Publisher<CatalogueElement> publisher) {
        PublishingChain
                .finalize(this)
                .require(unitOfMeasure)
                .add(dataType)
                .run(publisher)
    }

    @Override
    protected PublishingChain prepareDraftChain(PublishingChain chain) {
        chain.add(this.dataElements).add(this.classifications)
    }

    @Override
    void afterMerge(CatalogueElement destination) {
        if (!(destination.instanceOf(ValueDomain))) {
            return
        }
        List<DataElement> dataElements = new ArrayList<DataElement>(dataElements)
        for (DataElement element in dataElements) {
            element.valueDomain = destination as ValueDomain
            FriendlyErrors.failFriendlySave(element)
        }
    }
}
