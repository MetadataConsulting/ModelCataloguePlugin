package uk.co.mc.core

class ValueDomain extends CatalogueElement  {

	String name
	String unitOfMeasure
	String regexDef
    String format
	String description	
	DataType dataType
	
    static constraints = {
		description nullable:true, maxSize: 2000
		unitOfMeasure nullable:true, maxSize: 255
        format nullable:true, maxSize: 255
		regexDef nullable:true, maxSize: 500
		name blank: false, size: 2..255
    }
	
	static mapping = {
		description type: 'text'
	}

}
