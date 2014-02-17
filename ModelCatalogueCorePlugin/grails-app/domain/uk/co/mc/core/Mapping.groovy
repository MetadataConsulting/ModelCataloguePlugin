package uk.co.mc.core

class Mapping {

    ValueDomain source
    ValueDomain destination

    String mapping

    def map(value) {
        mapValue(mapping, value)
    }

    static constraints = {
        source nullable: false, unique: ['destination']
        destination nullable: false
        mapping nullable: false, blank: false, maxSize: 10000, validator: { val, obj ->
            if (!val) return true
            return validateMapping(val)
        }
    }

    static Mapping map(ValueDomain source, ValueDomain destination, String mapping) {
        if (!source || !source.id || !destination || !destination.id || !mapping) return null
        Mapping existing = findBySourceAndDestination(source, destination)
        if (existing) {
            return existing
        }
        Mapping newOne = new Mapping(source: source, destination: destination, mapping: mapping)
        newOne.save()
        source.addToOutgoingMappings(newOne)
        destination.addToIncomingMappings(newOne)
        newOne
    }

    static Mapping map(ValueDomain source, ValueDomain destination, Map mapping) {
        map(source, destination, createMappingFunctionFromMap(mapping))
    }


    static Mapping unmap(ValueDomain source, ValueDomain destination) {
        Mapping old = findBySourceAndDestination(source, destination)
        if (!old) return null
        source.removeFromOutgoingMappings(old)
        destination.removeFromIncomingMappings(old)
        old.delete()

        old
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

    static String createMappingFunctionFromMap(Map map) {
        "${map.collectEntries { key, value -> [key, "\"${value}\""] }.toMapString()}[x]"
    }

    static Object mapValue(String mapping, Object value) {
        new GroovyShell(new Binding(x: value)).evaluate(mapping)
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, source: ${source}, destination: ${destination}]"
    }

}
