package uk.co.mc.core.util.marshalling

class CustomObjectMarshallers {

    List<AbstractMarshallers> marshallers = []

    void register() {
        marshallers.each { it.register() }
    }

}
