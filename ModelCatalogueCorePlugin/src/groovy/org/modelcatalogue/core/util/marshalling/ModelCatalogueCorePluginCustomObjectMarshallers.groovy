package org.modelcatalogue.core.util.marshalling

import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.modelcatalogue.core.RelationshipTypeService
import org.modelcatalogue.core.reports.ReportsRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AutowireCapableBeanFactory

class ModelCatalogueCorePluginCustomObjectMarshallers {

    @Autowired ReportsRegistry reportsRegistry
    @Autowired LinkGenerator linkGenerator
    @Autowired RelationshipTypeService relationshipTypeService
    @Autowired AutowireCapableBeanFactory autowireCapableBeanFactory

    List<AbstractMarshallers> marshallers = []

    void register() {
        marshallers.each {
            autowireCapableBeanFactory.autowireBean(it)
            it.register()
        }
    }

}
