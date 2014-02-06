package uk.co.mc.core

class DataType extends CatalogueElement{

    static constraints = {
		name unique:true, size: 2..255
    }

}
