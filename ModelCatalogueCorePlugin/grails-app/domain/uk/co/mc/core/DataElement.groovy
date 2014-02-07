package uk.co.mc.core


/*
* A data element is an atomic unit of data with can be derived from other data sources
*
* */

class DataElement extends ExtendibleElement {

    //nearly all examples that I have seen have a unique data element code
    String code

    static constraints = {
        code nullable:true, unique:true, maxSize: 255
    }

    static mapping = {
    }
	
}
