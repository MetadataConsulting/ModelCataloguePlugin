package org.modelcatalogue.core.ddl

import static org.modelcatalogue.core.util.HibernateHelper.*
import grails.util.Holders
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.util.FriendlyErrors

class UpdateAction {

    final DataDefinitionLanguage ddl
    final String property
    final CatalogueElement element

    UpdateAction(DataDefinitionLanguage ddl, String property, CatalogueElement element) {
        this.ddl = ddl
        this.property = property
        this.element = element
    }

    void to(Object newValue) {
        GrailsDomainClass clazz = Holders.grailsApplication.getDomainClass(getEntityClass(element).name)
        if (element.hasProperty(property)) {
            GrailsDomainClassProperty prop = clazz.getPersistentProperty(property)
            if (prop.association) {
                element.setProperty(property, newValue ? ddl.find(prop.referencedDomainClass.clazz, newValue.toString()) : null)
            } else {
                element.setProperty(property, newValue)
            }
            FriendlyErrors.failFriendlySave(element)
            return
        }
        element.ext.put(property, newValue?.toString())
    }

    void add(Map<String, Object> ext, String name) {
        RelationshipType type = RelationshipType.readByName(property)
        element.createLinkTo(ddl.find(type.destinationClass, name), type, metadata: ext.collectEntries {
            [it.key, it.value?.toString()]
        })
    }

    void add(String name) {
        RelationshipType type = RelationshipType.readByName(property)
        element.createLinkTo(ddl.find(type.destinationClass, name), type)
    }

    void remove(String name) {
        RelationshipType type = RelationshipType.readByName(property)
        element.removeLinkTo(ddl.find(type.destinationClass, name), type)
    }
}