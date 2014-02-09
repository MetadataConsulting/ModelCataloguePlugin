package uk.co.mc.core

/**
 * Created by adammilward on 07/02/2014.
 *
 * Ontologolic relationship types can exist between data elements i.e. useFor, broaderTerms, narrowerTerm etc.
 * This allows the user to create relationships between their data elements building up a knowledge base
 * FIXME - we probably want to preload certain ontologyrelationships i.e. thesauri, skos
 *
 *
 */
class OntologyRelationshipType extends RelationshipType{

    //name of the relationship type i.e. parentChild  or synonym
    String name

    //the both sides of the relationship ie. for parentChild this would be parent (for synonym this is synonym, so the same on both sides)
    String sourceToDestination

    //the both sides of the relationship i.e. for parentChild this would be child (for synonym this is synonym, so the same on both sides)
    String destinationToSource

    //you can constrain the relationship type
    Class sourceClass

    // you can constrain the relationship type
    Class destinationClass

    //this is the rule that describes the relationship in terms of X and Y
    //for instance this could be custom validation, display etc.....i.e. if the relationship
    //type is a mandatory data element then the catalogue x must contain a value for the data element y

    //Rule relationshipTypeRule

    static constraints = {
        def classValidator = {val, obj ->
            if (!val) return true
            if (!CatalogueElement.isAssignableFrom(val)) return "Only uk.co.mc.core.CatalogueElement child classes are allowed"
            return true
        }
        name unique:true, maxSize: 255
        sourceToDestination maxSize: 255
        destinationToSource maxSize: 255
        sourceClass validator: classValidator
        destinationClass validator: classValidator
    }



    boolean validateSourceDestination(source, destination){
        if(!sourceClass.isInstance(source)){ return false }
        if(!destinationClass.isInstance(destination)){return false}
        return true
    }
}
