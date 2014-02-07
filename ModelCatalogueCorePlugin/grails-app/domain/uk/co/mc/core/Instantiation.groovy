package uk.co.mc.core

/**
 * Created by adammilward on 07/02/2014.
 * context exists between
 */
class Instantiation extends RelationshipType{

    //name of the relationship type i.e. parentChild  or synonym
    static String name = "instantiation"
    static String sourceToDestination = "instantiated by"
    static String destinationToSource = "instantiates"

    //you can constrain the relationship type
    static Class sourceClass = DataElement

    // you can constrain the relationship type
    static Class destinationClass = ValueDomain

}
