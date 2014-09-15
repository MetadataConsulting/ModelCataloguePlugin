package org.modelcatalogue.core

import org.modelcatalogue.core.util.SecuredRuleExecutor
import org.springframework.validation.Errors

class Mapping {

    // placeholder for situation where no mapping is expected
    // do not save of modify this value
    // static final Mapping DIRECT_MAPPING = new Mapping(mapping: 'x')

    static Mapping getDIRECT_MAPPING() { new Mapping(mapping: 'x') }

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
        except = ['source', 'destination']
    }

    CatalogueElement source
    CatalogueElement destination

    String mapping

    def map(value) {
        mapValue(mapping, value)
    }

    static belongsTo = [source: CatalogueElement, destination: CatalogueElement]

    static constraints = {
        source nullable: false, unique: ['destination']
        destination nullable: false, validator: { val, obj ->
            if (!val || !obj.source) return true
            return val != obj.source && val.class == obj.source.class
        }
        mapping nullable: false, blank: false, maxSize: 10000, validator: { val, obj, Errors errors ->
            if (!val) return true
            SecuredRuleExecutor.ValidationResult result = validateMapping(val)
            if (result) return true
            errors.rejectValue 'mapping', "wontCompile", [result.compilationFailedMessage] as Object[],  "Mapping compilation failed:\n {0}"
        }
    }

    static SecuredRuleExecutor.ValidationResult validateMapping(String mappingText) {
        new SecuredRuleExecutor(x: 0).validate(mappingText)
    }

    static Object mapValue(String mapping, Object value) {
        new SecuredRuleExecutor(x: value).execute(mapping)
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, source: ${source}, destination: ${destination}]"
    }

    def beforeDelete(){
        if (source) {
            source?.removeFromOutgoingMappings(this)
        }
        if(destination){
            destination?.removeFromIncomingMappings(this)
        }
    }

}
