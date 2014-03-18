package org.modelcatalogue.core

class Mapping {

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
        except = ['source', 'destination']
    }

    ValueDomain source
    ValueDomain destination

    String mapping

    def map(value) {
        mapValue(mapping, value)
    }

    static constraints = {
        source nullable: false, unique: ['destination']
        destination nullable: false, validator: { val, obj ->
            if (!val || !obj.source) return true
            return val != obj.source
        }
        mapping nullable: false, blank: false, maxSize: 10000, validator: { val, obj ->
            if (!val) return true
            return validateMapping(val)
        }
    }

    static boolean validateMapping(String mappingText) {
        try {
            GroovyShell shell = new GroovyShell(new Binding(x: 0))
            shell.evaluate(mappingText)
            return true
        } catch (ignore) {
            return false
        }
    }

    static Object mapValue(String mapping, Object value) {
        new GroovyShell(new Binding(x: value)).evaluate(mapping)
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, source: ${source}, destination: ${destination}]"
    }

}
