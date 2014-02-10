package uk.co.mc.core


/*
* A data element is an atomic unit of data
* i.e. xml  <xs:element name="title" />
*
* */

class DataElement extends ExtendibleElement {

    //nearly all examples that we are working with have a unique data element code i.e. NHIC105495432
    //however conceptually this should not be mandatory
    String code

    static constraints = {
        code nullable:true, unique:true, maxSize: 255
    }


    static transients = ['containedIn', 'hasContextOf']

    List/*<DataElement>*/ getContainedIn() {
        getIncomingRelationsByType(RelationshipType.containmentType)
    }

    Relationship addToContainedIn(Model model) {
        createLinkFrom(model, RelationshipType.containmentType)
    }

    void removeFromContainedIn(Model model) {
        removeLinkFrom(model, RelationshipType.containmentType)
    }
	
}
