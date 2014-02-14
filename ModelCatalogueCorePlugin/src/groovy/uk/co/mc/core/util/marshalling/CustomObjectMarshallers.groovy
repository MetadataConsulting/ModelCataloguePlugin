package uk.co.mc.core.util.marshalling

class CustomObjectMarshallers {

    List<MarshallersProvider> marshallers = []

    void register() {
        marshallers.each { it.register() }
    }

}
