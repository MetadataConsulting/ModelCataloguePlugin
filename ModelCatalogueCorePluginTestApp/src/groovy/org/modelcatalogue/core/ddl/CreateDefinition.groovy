package org.modelcatalogue.core.ddl

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.util.FriendlyErrors

class CreateDefinition<T extends CatalogueElement> {

    final DataDefinitionLanguage ddl
    final Class<T> domain

    CreateDefinition(DataDefinitionLanguage ddl, Class<T> domain) {
        this.ddl = ddl
        this.domain = domain
    }

    void called(Map<String, Object> props = [:], String name) {
        T existing = null

        try {
            existing = ddl.find(domain, name)
        } catch(IllegalArgumentException ignored) {}

        if (existing) {
            throw new IllegalArgumentException("${GrailsNameUtils.getNaturalName(domain.simpleName)} called '${name}' already exists")
        }

        T element = domain.newInstance()
        element.name = name

        for (Map.Entry<String, Object> entry in props) {
            if (element.hasProperty(entry.key)) {
                element.setProperty(entry.key, entry.value)
            } else {
                element.ext[entry.key] = entry.value
            }
        }

        if (ddl.dataModel) {
            element.dataModel = ddl.dataModel
        }
        FriendlyErrors.failFriendlySave(element)
    }
}