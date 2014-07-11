package org.modelcatalogue.core.util.marshalling

import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.modelcatalogue.core.RelationshipTypeService
import org.modelcatalogue.core.reports.ReportsRegistry
import org.springframework.beans.factory.annotation.Autowired

class ModelCatalogueCorePluginCustomObjectMarshallers {

    @Autowired ReportsRegistry reportsRegistry
    @Autowired LinkGenerator linkGenerator
    @Autowired RelationshipTypeService relationshipTypeService

    List<AbstractMarshallers> marshallers = []

    void register() {
        marshallers.each {
            it.register()
            if (it.hasProperty('reportsRegistry')) {
                it.reportsRegistry = reportsRegistry
            }
            if (it.hasProperty('linkGenerator')) {
                it.linkGenerator = linkGenerator
            }
            if (it.hasProperty('relationshipTypeService')) {
                it.relationshipTypeService = relationshipTypeService
            }
        }
    }

}
