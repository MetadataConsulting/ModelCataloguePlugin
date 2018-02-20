package org.modelcatalogue.core

import org.modelcatalogue.core.scripting.MappingScript
import org.modelcatalogue.core.util.SecuredRuleExecutor
import org.springframework.validation.Errors

class Mapping {

    // placeholder for situation where no mapping is expected
    // do not save of modify this value
    // static final Mapping DIRECT_MAPPING = new Mapping(mapping: 'x')

    static Mapping getDIRECT_MAPPING() { new Mapping(mapping: 'x') }

    CatalogueElement source
    CatalogueElement destination

    String mapping

    def map(value) {
        mapValue(mapping, value)
    }

    /**
     * Creates new reusable mapping. Execute the mapper with <code>execute(x: valueToBeMapped)</code>
     * to get the mapped value.
     * @return reusable mapping
     */
    SecuredRuleExecutor.ReusableScript mapper() {
        reusableMapping(mapping)
    }

    static belongsTo = [source: CatalogueElement, destination: CatalogueElement]

    static constraints = {
        source nullable: false, unique: ['destination']
        destination nullable: false, validator: { val, obj ->
            if (!val || !obj.source) return true
            return val != obj.source && (val.class == obj.source.class || obj.source.instanceOf(DataType) && val.instanceOf(DataType))
        }
        mapping nullable: false, blank: false, maxSize: 10000, validator: { val, obj, Errors errors ->
            if (!val) return true
            SecuredRuleExecutor.ValidationResult result = validateMapping(val)
            if (result) return true
            errors.rejectValue 'mapping', "wontCompile", [result.compilationFailedMessage] as Object[],  "Mapping compilation failed:\n {0}"
        }
    }

    static SecuredRuleExecutor.ValidationResult validateMapping(String mappingText) {
        new SecuredRuleExecutor(MappingScript, x: 0).validate(mappingText)
    }

    static Object mapValue(String mapping, Object value) {
        new SecuredRuleExecutor(MappingScript, x: value).execute(mapping)
    }

    static SecuredRuleExecutor.ReusableScript reusableMapping(String mappingText) {
        new SecuredRuleExecutor(MappingScript, x: 0).reuse(mappingText)
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, source: ${source}, destination: ${destination}]"
    }

    /**
     * Removes relationship from {@link #source} and {@link #destination}. This method causes {@link Mapping} object
     * to be in inconsistent state and should be use before its delete.
     */
    def clearRelationships(){
        if (source) {
            source.removeFromOutgoingMappings(this)
        }
        if (destination) {
            destination.removeFromIncomingMappings(this)
        }
    }
}
