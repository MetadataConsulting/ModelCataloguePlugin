package uk.co.mc.core

class DataElement extends CatalogueElement {
	
	String name
	
	String description
	
	String definition

    static constraints = {
		description nullable:true
		definition nullable: true
		name blank: false
    }
	
	static mapping = {
		description type: "text"
		definition type: "text"
	}
	

	
	

	
}
