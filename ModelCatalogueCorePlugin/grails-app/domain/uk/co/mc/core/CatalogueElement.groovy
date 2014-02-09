package uk.co.mc.core


/*
* Catalogue Element - there are a number of catalogue elements that make up the model catalogue (please see
* DataType, ConceptualDomain, MeasurementUnit, Model, ValueDomain, DataElement)
* they extend catalogue element which allows creation of incoming and outgoing
* relationships between them. They also  share a number of characteristics.
* */

abstract class CatalogueElement {

    String name
    String description
		
	static hasMany = [ incomingRelationships: Relationship, outgoingRelationships: Relationship  ]
		
    static constraints = {
        name size: 2..255
        description nullable:true, maxSize:2000
	}

    static mapping = {
        description type: "text"
    }


    static mappedBy = [ outgoingRelationships: 'source', incomingRelationships: 'destination']


		/******************************************************************************************************************/
		/****functions for specifying relationships between catalogue elements using the uk.co.mc.core.Relationship class ************/
		/******************************************************************************************************************/
		
		/***********return all the relations************/
		
		List getRelations() {
		
			//array of relations to return to the caller
			/*def relationsR = []

			
			relations.each{ relation ->
				def objectId
                def relationshipDirection
                def relationshipType = relation.relationshipType
				
				if(relation.destination == this.id){
					objectId = relation.objectXId
                    if(relationshipType.sourceToDestination){
                        relationshipDirection = relationshipType.sourceToDestination
                    }
				}else{
					objectId = relation.destination
                    if(relationshipType.destinationToSource){
                        relationshipDirection = relationshipType.destinationToSource
                    }
				}
				
				def catalogueElement = CatalogueElement.get(objectId)
				catalogueElement.relationshipType = relationshipType
                catalogueElement.relationshipDirection = relationshipDirection
				relationsR.add(catalogueElement)
			}
		

			return relationsR*/
		
		}
		
		/***********return the relations with the given relationship type name***********/
//
//		List getRelations(String relationTypeName) {
//
//				//array of relations to return to the caller
//				def relationsR = []
//
//
//				if(relationTypeName){
//
//					def relationshipType = RelationshipType.findByName(relationTypeName)
//                    def relationshipDirection = relationshipType
//                    if(relationshipType){
//
//                        relations.each{ relation ->
//                            if(relation.relationshipType.id==relationshipType.id){
//                                def objectId
//
//                                //if the relation y side is this object then return the x side of the relationship otherwise return the y side
//                                if(relation.destination == this.id){
//                                    objectId = relation.objectXId
//
//                                    //if the relationship type has an sourceToDestination then return this instead of the relationship type name
//                                    //i.e. if the relationship is parentChild and the object to return is the parent, then return Parent rather
//                                    //then ParentChild as the relationshipT type
//
//                                    if(relationshipType.sourceToDestination){
//                                        relationshipDirection = relationshipType.sourceToDestination
//                                    }
//
//
//                                }else{
//
//                                    objectId = relation.destination
//
//                                    //if the relationship type has an destinationToSource then return this instead of the relationship type name
//                                    //i.e. if the relationship is parentChild and the object to return is the child, then return Child rather
//                                    //then ParentChild as the relationship type
//
//                                    if(relationshipType.destinationToSource){
//                                        relationshipDirection = relationshipType.destinationToSource
//                                    }
//
//
//
//                                }
//
//
//
//                                def catalogueElement = CatalogueElement.get(objectId)
//                                catalogueElement.relationshipType = relationTypeName
//                                catalogueElement.relationshipDirection = relationshipDirection
//                                relationsR.add(catalogueElement)
//                            }
//
//                        }
//                    }
//
//				}
//
//				return relationsR
//
//			}
//
//
//
//		public void addToRelations(Object relation, RelationshipType relationshipType){
//           Relationship.link(this, relation, relationshipType)
//		}



}