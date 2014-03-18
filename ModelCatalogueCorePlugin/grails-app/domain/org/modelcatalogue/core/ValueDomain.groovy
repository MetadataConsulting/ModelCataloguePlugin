package org.modelcatalogue.core

import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

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

    static elasticGormSearchable = {
        name boost:5
        regexDef index:'no'
        dataType component:true
        unitOfMeasure component:true
        incomingRelationships component: true
        outgoingRelationships component: true
        incomingMappings component: true
        outgoingMappings component: true

    }

    static searchable = false

    //FIXME valueDomain needs to be unique within a conceptual domain

	MeasurementUnit unitOfMeasure
	String regexDef
	DataType dataType

    static hasMany  = [ outgoingMappings: Mapping,  incomingMappings: Mapping ]
    static mappedBy = [ outgoingMappings: 'source', incomingMappings: 'destination']

    static constraints = {
		description nullable:true, maxSize: 2000
		unitOfMeasure nullable:true, maxSize: 255
		regexDef nullable:true, maxSize: 500, validator: { val,obj ->
            if(!val){return true}
            try{
                Pattern.compile(val)
            }catch(PatternSyntaxException e){
                return ['wontCompile', e.message]
            }
            return true
        }
    }


    static transients = ['includedIn', 'instantiates']


    List/*<DataElement>*/ getIncludedIn() {
        getIncomingRelationsByType(RelationshipType.inclusionType)
    }

    Relationship addToIncludedIn(ConceptualDomain conceptualDomain) {
        createLinkFrom(conceptualDomain, RelationshipType.inclusionType)
    }

    void removeFromIncludedIn(ConceptualDomain conceptualDomain) {
        removeLinkFrom(conceptualDomain, RelationshipType.inclusionType)
    }

    //INSTANTIATION


    List/*<DataElement>*/ getInstantiates() {
        getIncomingRelationsByType(RelationshipType.instantiationType)
    }

    Relationship addToInstantiates(DataElement dataElement) {
        createLinkFrom(dataElement, RelationshipType.instantiationType)
    }

    void removeFromInstantiates (DataElement dataElement) {
        removeLinkFrom(dataElement, RelationshipType.instantiationType)
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
