package org.modelcatalogue.core.util.marshalling

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AutowireCapableBeanFactory

class ModelCatalogueCorePluginCustomObjectMarshallers {

    @Autowired AutowireCapableBeanFactory autowireCapableBeanFactory

    List<AbstractMarshaller> marshallers = []

    void register() {
        for ( AbstractMarshaller marshaller : marshallers ) {
            autowireCapableBeanFactory.autowireBean(marshaller)
            marshaller.register()
        }
    }

}
