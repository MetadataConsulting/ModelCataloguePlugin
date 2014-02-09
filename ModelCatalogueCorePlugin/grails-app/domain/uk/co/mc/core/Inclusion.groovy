package uk.co.mc.core

/**
 * Created by adammilward on 07/02/2014.
 * The Inclusion relationship type allows value domains to be included in conceptual domains
 *
 *
 */
class Inclusion extends RelationshipType{

    //name of the relationship type i.e. parentChild  or synonym
    public final String name = "include"
    public final String sourceToDestination = "includes"
    public final String destinationToSource = "included in"

    //you can constrain the relationship type
    public final Class sourceClass = ConceptualDomain

    // you can constrain the relationship type
    public final Class destinationClass = ValueDomain

    boolean validateSourceDestination(source, destination){
        if(!ConceptualDomain.isInstance(source)){ return false }
        if(!ValueDomain.isInstance(destination)){return false}
        return true
    }

}
