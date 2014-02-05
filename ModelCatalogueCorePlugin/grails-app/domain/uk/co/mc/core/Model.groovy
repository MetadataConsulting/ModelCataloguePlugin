package uk.co.mc.core

class Model extends CatalogueElement  {
	 
	String name
	String description


	static hasMany = [relations: Relationship]
	 
    static constraints = {
		name blank: false
    }
	
	static mapping = {
		description type: 'text'
	}
	

}
