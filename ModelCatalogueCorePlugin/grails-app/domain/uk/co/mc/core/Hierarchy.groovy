package uk.co.mc.core

/**
 * Created by adammilward on 07/02/2014.
 * context exists between
 */
class Hierarchy extends RelationshipType{

    //name of the relationship type i.e. parentChild  or synonym
    static String name = "hierarchy"
    static String sourceToDestination = "parent of"
    static String destinationToSource = "child of"

    //you can constrain the relationship type
    static Class sourceClass = Model

    // you can constrain the relationship type
    static Class destinationClass = Model

}
