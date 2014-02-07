package uk.co.mc.core

/**
 * Created by adammilward on 07/02/2014.
 * context exists between
 */
class Context extends RelationshipType{

    //name of the relationship type i.e. parentChild  or synonym
    static String name = "context"
    static String sourceToDestination = "provides context for"
    static String destinationToSource = "has context of"

    //you can constrain the relationship type
    static Class sourceClass = ConceptualDomain

    // you can constrain the relationship type
    static Class destinationClass = Model

}
