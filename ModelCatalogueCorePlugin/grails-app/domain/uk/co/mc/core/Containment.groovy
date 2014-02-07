package uk.co.mc.core

/**
 * Created by adammilward on 07/02/2014.
 * context exists between
 */
class Containment extends RelationshipType{

    //name of the relationship type i.e. parentChild  or synonym
    static String name = "containment"
    static String sourceToDestination = "contains"
    static String destinationToSource = "contained in"

    //you can constrain the relationship type
    static Class sourceClass = Model

    // you can constrain the relationship type
    static Class destinationClass = DataElement

}
