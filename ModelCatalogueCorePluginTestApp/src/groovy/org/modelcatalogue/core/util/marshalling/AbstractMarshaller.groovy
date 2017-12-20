package org.modelcatalogue.core.util.marshalling

import grails.converters.JSON
import org.modelcatalogue.core.SecurityService
import org.modelcatalogue.core.reports.ReportDescriptor
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.security.DataModelAclService
import org.springframework.beans.factory.annotation.Autowired

/**
 * Interface for the classes providing custom marshallers.
 * <p/>
 * The marshallers are registered in the {@link AbstractMarshaller#register()} method.
 */
abstract class AbstractMarshaller {

    @Autowired DataModelAclService dataModelAclService
    @Autowired JsonMarshallingCustomizerRegistry jsonMarshallingCustomizerRegistry

    final Class type

    AbstractMarshaller(Class type) {
        this.type = type
    }

    final void register() {
        JSON.registerObjectMarshaller(type) { el ->
            if (!el) return null
            jsonMarshallingCustomizerRegistry.postProcessJson(el, prepareJsonMap(el))
        }
    }

    abstract protected Map<String, Object> prepareJsonMap(element)
}
