package uk.co.mc.core

/**
 * Created by adammilward on 07/02/2014.
 * context exists between
 */
class Mapping extends RelationshipType{

    //name of the relationship type i.e. parentChild  or synonym
    static String name = "mapping"
    static String sourceToDestination = "maps to"
    static String destinationToSource = "maps to"

    //you can constrain the relationship type
    static Class sourceClass = DataType

    // you can constrain the relationship type
    static Class destinationClass = DataType

    //FIXME this class needs to include a map object that maps the two data types

}
