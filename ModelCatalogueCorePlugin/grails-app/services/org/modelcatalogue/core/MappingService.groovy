package org.modelcatalogue.core


class MappingService {

    def auditService

    static transactional = true

    Mapping map(CatalogueElement source, CatalogueElement destination, String mapping) {
        if (!source || !source.id || !destination || !destination.id || !mapping) return null
        Mapping existing = Mapping.findBySourceAndDestination(source, destination)
        if (existing) {
            existing.mapping = mapping
            existing.validate()

            if (existing.hasErrors()) {
                return existing
            }

            return existing.save(flush: true)
        }
        Mapping newOne = new Mapping(source: source, destination: destination, mapping: mapping)
        newOne.validate()

        if (newOne.hasErrors()) {
            return newOne
        }

        newOne.save(flush: true)
        source.addToOutgoingMappings(newOne).save()
        destination.addToIncomingMappings(newOne).save()
        newOne
    }

    Mapping map(CatalogueElement source, CatalogueElement destination, Map mapping) {
        map(source, destination, createMappingFunctionFromMap(mapping))
    }


    Mapping unmap(CatalogueElement source, CatalogueElement destination) {
        Mapping old = Mapping.findBySourceAndDestination(source, destination)
        if (!old) return null

        auditService.logMappingDeleted(old)

        source.removeFromOutgoingMappings(old).save()
        destination.removeFromIncomingMappings(old).save()
        old.delete(flush: true)
        old
    }


    private static String createMappingFunctionFromMap(Map map) {
        "${map.collectEntries { key, value -> [key, "\"${value}\""] }.toMapString()}[x]"
    }


}
