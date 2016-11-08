package org.modelcatalogue.core.util.marshalling

import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct

abstract class JsonMarshallingCustomizer {

    @Autowired JsonMarshallingCustomizerRegistry jsonMarshallingCustomizerRegistry

    abstract def customize(element, json)

    @PostConstruct void autoRegister() {
        jsonMarshallingCustomizerRegistry.customizers.add this
    }

}
