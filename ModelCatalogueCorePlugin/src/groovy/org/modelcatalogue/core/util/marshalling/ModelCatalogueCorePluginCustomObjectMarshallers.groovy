package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.reports.ReportsRegistry
import org.springframework.beans.factory.annotation.Autowired

class ModelCatalogueCorePluginCustomObjectMarshallers {

    @Autowired ReportsRegistry reportsRegistry

    List<AbstractMarshallers> marshallers = []

    void register() {
        marshallers.each {
            it.register()
            if (it.hasProperty('reportsRegistry')) {
                it.reportsRegistry = reportsRegistry
            }
        }
    }

}
