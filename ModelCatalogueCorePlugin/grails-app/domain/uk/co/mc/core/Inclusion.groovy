package uk.co.mc.core

/**
 * Created by adammilward on 07/02/2014.
 * context exists between
 */
class Inclusion extends RelationshipType{

    //name of the relationship type i.e. parentChild  or synonym
    static String name = "include"
    static String sourceToDestination = "includes"
    static String destinationToSource = "included in"

    //you can constrain the relationship type
    static Class sourceClass = ConceptualDomain

    // you can constrain the relationship type
    static Class destinationClass = ValueDomain

}
