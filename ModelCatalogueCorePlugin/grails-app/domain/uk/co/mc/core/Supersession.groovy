package uk.co.mc.core

/**
 * Created by adammilward on 07/02/2014.
 * context exists between
 */
class Supersession extends RelationshipType{

    //name of the relationship type i.e. parentChild  or synonym
    static String name = "supersession"
    static String sourceToDestination = "superseded by"
    static String destinationToSource = "supersedes"

    //you can constrain the relationship type
    Class sourceClass

    // you can constrain the relationship type
    Class destinationClass

    static constraints = {

        sourceClass validator: {val, obj ->
            if (!val) return true
            if (!PublishedElement.isAssignableFrom(val)){
                return "Only uk.co.mc.core.PublishedElement child classes are allowed"
            }
            if(val!=obj.destinationClass){
                return "source class must match the destination class "
            }
            return true
        }
        destinationClass validator: {val, obj ->
            if (!val) return true
            if (!PublishedElement.isAssignableFrom(val)){
                return "Only uk.co.mc.core.PublishedElement child classes are allowed"
            }
            if(val!=obj.sourceClass){
                return "source class must match the destination class "
            }
        }

    }

}
