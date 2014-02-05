package uk.co.mc.core

class DataElement extends CatalogueElement {
	
	String name
	
	String description
	
	String definition

    static constraints = {
		description nullable:true, maxSize:2000
		definition nullable: true, maxSize:2000
		name blank: false, size: 2..255
    }
	
	static mapping = {
		description type: "text"
		definition type: "text"
	}
	

	
	

	
}
