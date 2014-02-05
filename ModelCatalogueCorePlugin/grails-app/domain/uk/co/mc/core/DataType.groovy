package uk.co.mc.core

class DataType  {
	
	String name
	
    static constraints = {
		name unique:true, maxSize: 255
    }

}
