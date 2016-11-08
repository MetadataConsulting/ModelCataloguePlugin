package org.modelcatalogue.core.util.marshalling

class JsonMarshallingCustomizerRegistry {

    List<JsonMarshallingCustomizer> customizers = []

    def postProcessJson(element, json) {
        def result = json
        for (JsonMarshallingCustomizer customizer in customizers) {
            result = customizer.customize(element, result)
        }
        result
    }

}
