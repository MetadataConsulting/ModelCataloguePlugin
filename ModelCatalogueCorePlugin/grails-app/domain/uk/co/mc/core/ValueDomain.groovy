package uk.co.mc.core

import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class ValueDomain extends CatalogueElement  {

	MeasurementUnit unitOfMeasure
	String regexDef
	DataType dataType
	
    static constraints = {
		description nullable:true, maxSize: 2000
		unitOfMeasure nullable:true, maxSize: 255
		regexDef nullable:true, maxSize: 500, validator: { val,obj ->
            if(!val){return true}
            try{
                Pattern.compile(val)
            }catch(PatternSyntaxException e){
                return ['wontCompile', e.message]
            }
            return true
        }
    }


}
