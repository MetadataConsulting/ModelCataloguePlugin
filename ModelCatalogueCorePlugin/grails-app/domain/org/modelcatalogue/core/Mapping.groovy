package org.modelcatalogue.core

import org.modelcatalogue.core.util.SecuredRuleExecutor

class Mapping {

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
        mapping nullable: false, blank: false, maxSize: 10000, validator: { val, obj ->
            if (!val) return true
            return validateMapping(val)
        }
    }

    static boolean validateMapping(String mappingText) {
        new SecuredRuleExecutor(x: 0).validate(mappingText)
    }

    static Object mapValue(String mapping, Object value) {
        new SecuredRuleExecutor(x: value).execute(mapping)
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, source: ${source}, destination: ${destination}]"
    }

}
