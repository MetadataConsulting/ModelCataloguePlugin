package uk.co.mc.core

class Relationship  {
	
	CatalogueElement source
    
	CatalogueElement destination
	
	RelationshipType relationshipType

    static constraints = {
        relationshipType validator: { val,obj ->

            if(!val) return true;
            if(!val.validateSourceDestination(obj.source,obj.destination)){
               return false;
            }
            return  true;

        }
    }




	
	static link(source, destination, relationshipType){


        if(source.id && destination.id && relationshipType.id){

             Relationship relationshipInstance = Relationship.findBySourceAndDestinationAndRelationshipType(source, destination, relationshipType)

             if(relationshipInstance){

                return relationshipInstance

            }

         }

        Relationship relationshipInstance = new Relationship(
                source: source.id ? source : null,
                destination: destination.id ? destination : null,
                relationshipType: relationshipType.id ? relationshipType : null
        )

        source.addToOutgoingRelationships(relationshipInstance)
        destination.addToIncomingRelationships(relationshipInstance)

        relationshipInstance.save(flush: true)


        relationshipInstance



	}


	static unlink(source, destination,relationshipType){


        if(source.id && destination.id && relationshipType.id){

            Relationship relationshipInstance = Relationship.findBySourceAndDestinationAndRelationshipType(source, destination, relationshipType)

            if(relationshipInstance){

               source.removeFromOutgoingRelationships(relationshipInstance)
               destination.removeFromIncomingRelationships(relationshipInstance)
               relationshipInstance.delete()
            }

        }

	}

}
