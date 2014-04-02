package org.modelcatalogue.core.util.marshalling

class ModelCatalogueCorePluginCustomObjectMarshallers {

    List<AbstractMarshallers> marshallers = []

    void register() {
        marshallers.each { it.register() }
    }

}
