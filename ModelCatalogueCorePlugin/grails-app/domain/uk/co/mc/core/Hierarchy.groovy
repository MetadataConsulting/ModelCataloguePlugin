package uk.co.mc.core

/**
 *
 * The Hierarchy relationship type allows a model to have parent and child models (please see Model)
 * i.e. a model of a book can have many chapters
 *
 *<xs:complexType name="book">
 *  <xs:complexType name="chapter1">
        <xs:element name="title"/>
    </xs:complexType>
    <xs:complexType name="chapter2">
         <xs:element name="title"/>
    </xs:complexType>
 </xs:complexType>
 *
 *
 */
class Hierarchy extends RelationshipType{

    //name of the relationship type i.e. parentChild  or synonym
    public final String name = "hierarchy"
    public final String sourceToDestination = "parent of"
    public final String destinationToSource = "child of"

    //you can constrain the relationship type
    public final Class sourceClass = Model

    // you can constrain the relationship type
    public final Class destinationClass = Model

    boolean validateSourceDestination(source, destination){
        if(!Model.isInstance(source)){ return false }
        if(!Model.isInstance(destination)){return false}
        return true
    }

}
