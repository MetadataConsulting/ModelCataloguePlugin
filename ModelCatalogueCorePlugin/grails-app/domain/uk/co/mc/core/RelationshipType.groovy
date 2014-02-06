package uk.co.mc.core

class RelationshipType {
	
	//name of the relationship type i.e. parentChild  or synonym
	String name
	
	//the both sides of the relationship ie. for parentChild this would be parent (for synonym this is synonym, so the same on both sides)
	String sourceToDestination
	
	//the both sides of the relationship i.e. for parentChild this would be child (for synonym this is synonym, so the same on both sides)
	String destinationToSource

    //this is nullable - you can constrain the relationship type
    Class sourceClass

    //this is nullable - you can constrain the relationship type
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

        if(!sourceClass.isInstance(source)){
            return false
        }

        if(!destinationClass.isInstance(destination)){
            return false
        }

        return true

    }
}
