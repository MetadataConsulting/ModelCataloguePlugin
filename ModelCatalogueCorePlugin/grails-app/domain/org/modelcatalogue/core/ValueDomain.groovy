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
*  *  * <xs:complexType name="PatientModelA">
<xs:sequence>
<xs:element name="name" type="xs:string"/>
<xs:element name="treatment" type="treatment"/>
</xs:sequence>
 </xs:complexType>

 *
 * !!!!!!!!VALUE DOMAINS Need to be related to at least one conceptual domain.....we need to build this into the
 * constraints
 *
 *
*/

class ValueDomain extends CatalogueElement  {

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
        name boost:5
        dataType component:true
        unitOfMeasure component:true
        incomingRelationships component: true
        outgoingRelationships component: true
        incomingMappings component: true
        outgoingMappings component: true
        except = ['includedIn', 'instantiates', 'regexDef']
    }

    //FIXME valueDomain needs to be unique within a conceptual domain

	MeasurementUnit unitOfMeasure
	String rule
	DataType dataType

    static hasMany  = [ outgoingMappings: Mapping,  incomingMappings: Mapping ]
    static mappedBy = [ outgoingMappings: 'source', incomingMappings: 'destination']
    static transients = ['regexDef']

    static constraints = {
		description nullable:true, maxSize: 2000
		unitOfMeasure nullable:true, maxSize: 255
		rule nullable:true, maxSize: 200, validator: { val,obj ->
            if(!val){return true}
            SecuredRuleExecutor.ValidationResult result = new SecuredRuleExecutor(x: null, domain: obj).validate(val)
            result ? true : ['wontCompile', result.compilationFailedMessage]
        }
    }


    static relationships = [
        incoming: [inclusion: 'includedIn', instantiation: 'instantiates']
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

//    public boolean equals(Object obj) {
//        if (!(obj instanceof ValueDomain)) {
//            return false;
//        }
//        if (this.is(obj)) {
//            return true;
//        }
//        ValueDomain cd = (ValueDomain) obj;
//        return new EqualsBuilder()
//                .append(name, cd.name)
//                .append(unitOfMeasure, cd.unitOfMeasure)
//                .append(dataType, cd.dataType)
//                .isEquals();
//    }
//
//    public int hashCode() {
//        return new HashCodeBuilder()
//                .append(name)
//                .append(unitOfMeasure)
//                .append(dataType)
//                .toHashCode();
//    }

}
