package uk.co.mc.core

class ConceptualDomain extends CatalogueElement  {

	String name
	String description

	static constraints = {
        name blank: false, size: 2..255
        description maxSize: 2000
    }

	static mapping = {
		description type: 'text'
	}
}
