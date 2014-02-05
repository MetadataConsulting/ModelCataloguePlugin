package uk.co.mc.core

class DataType  {
	
	String name
	
    static constraints = {
		name unique:true, size: 2..255
    }

}
