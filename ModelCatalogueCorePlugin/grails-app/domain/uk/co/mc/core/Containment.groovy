package uk.co.mc.core

/**
 *
 * Models contain data elements (please see Model & DataElement)
 * Containment is the relationship type that specifies that a data element is contained in a model
 * and a model contains a data element. This allows different models to contain the same element
 * i.e. a model of a book and a model of a paper may contain some of the same data elements
 * (title and author) and not others (isbn)
 *<xs:complexType name="book">
 *     <xs:element name="title"/>
 *     <xs:element name="author"/>
 *     <xs:element name="isbn"/>
 *</xs:complexType>
 *
 *  *<xs:complexType name="paper">
 *     <xs:element name="title"/>
 *     <xs:element name="author"/>
 * </xs:complexType>

 *
 */
class Containment extends RelationshipType{

    //name of the relationship type i.e. parentChild  or synonym
    public final String name = "containment"
    public final String sourceToDestination = "contains"
    public final String destinationToSource = "contained in"

    //you can constrain the relationship type
    public final Class sourceClass = Model

    // you can constrain the relationship type
    public final Class destinationClass = DataElement

    static constraints = {
    }

    boolean validateSourceDestination(source, destination){
        if(!Model.isInstance(source)){ return false }
        if(!DataElement.isInstance(destination)){return false}
        return true
    }


}
