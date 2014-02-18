package uk.co.mc.core.util.marshalling

import grails.converters.JSON
import grails.converters.XML

/**
 * Interface for the classes providing custom marshallers.
 * <p/>
 * The marshallers are registered in the {@link AbstractMarshallers#register()} method.
 */
abstract class AbstractMarshallers {

    final Class type

    AbstractMarshallers(Class type) {
        this.type = type
    }

    final void register() {
        JSON.registerObjectMarshaller(type) { el ->
            if (!el) return null
            prepareJsonMap(el)
        }
        XML.registerObjectMarshaller(type) { el, XML xml ->
            if (!el) return
            addXmlAttributes(el, xml)
            buildXml(el, xml)
        }
    }

    abstract protected Map<String, Object> prepareJsonMap(element)

    protected void buildXml(element, XML xml) {}

    protected void addXmlAttributes(element, XML xml) {}

}
