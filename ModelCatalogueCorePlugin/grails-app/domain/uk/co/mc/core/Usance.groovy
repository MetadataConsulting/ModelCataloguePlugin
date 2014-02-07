package uk.co.mc.core

/**
 * Created by adammilward on 07/02/2014.
 * context exists between
 */
class Usance extends RelationshipType{

    //name of the relationship type i.e. parentChild  or synonym
    static String name = "usance"
    static String sourceToDestination = "uses"
    static String destinationToSource = "used by"

    //you can constrain the relationship type
    static Class sourceClass = ValueDomain

    // you can constrain the relationship type
    static Class destinationClass = DataType

}
