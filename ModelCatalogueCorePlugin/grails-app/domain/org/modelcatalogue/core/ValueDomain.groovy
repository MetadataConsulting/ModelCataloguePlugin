package org.modelcatalogue.core

import org.modelcatalogue.core.util.SecuredRuleExecutor

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

class ValueDomain extends ExtendibleElement  {

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
        name boost:5
        dataType component:true
        unitOfMeasure component:true
        extensions component:true

        except = ['incomingRelationships', 'outgoingRelationships']
    }

	MeasurementUnit unitOfMeasure
	String rule

    Boolean multiple = Boolean.FALSE

    static belongsTo = [dataType: DataType]
    static transients = ['regexDef']

    static constraints = {
		description     nullable:true, maxSize: 2000
		unitOfMeasure   nullable:true
        dataType        nullable: true

		rule nullable:true, maxSize: 200, validator: { val,obj ->
            if(!val){return true}
            SecuredRuleExecutor.ValidationResult result = new SecuredRuleExecutor(x: null, domain: obj).validate(val)
            result ? true : ['wontCompile', result.compilationFailedMessage]
        }
    }


    static relationships = [
        incoming: [inclusion: 'includedIn', instantiation: 'instantiates', base: 'basedOn', union: 'unitedIn'],
        outgoing: [base: 'isBaseFor', union: 'unionOf']
    ]

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

    boolean validateRule(Object x) {
        new SecuredRuleExecutor(x: x, domain: this).execute(rule)
    }


    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }


}
