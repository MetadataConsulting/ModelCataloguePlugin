package uk.co.mc.core

/**
 * Created by adammilward on 07/02/2014.
 * Instanciation is a relationship type.
 * DataElements can be instantiated by ValueDomains
 *
 *
 */
class Instantiation extends RelationshipType{

    //name of the relationship type i.e. parentChild  or synonym
    public final String name = "instantiation"
    public final String sourceToDestination = "instantiated by"
    public final String destinationToSource = "instantiates"

    //you can constrain the relationship type
    public final Class sourceClass = DataElement

    // you can constrain the relationship type
    public final Class destinationClass = ValueDomain

    boolean validateSourceDestination(source, destination){
        if(!DataElement.isInstance(source)){ return false }
        if(!ValueDomain.isInstance(destination)){return false}
        return true
    }

}
