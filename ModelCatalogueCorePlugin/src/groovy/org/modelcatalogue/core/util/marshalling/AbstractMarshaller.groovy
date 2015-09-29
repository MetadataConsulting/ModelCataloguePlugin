package org.modelcatalogue.core.util.marshalling

import grails.converters.JSON
import org.modelcatalogue.core.SecurityService
import org.modelcatalogue.core.reports.ReportDescriptor
import org.springframework.beans.factory.annotation.Autowired

/**
 * Interface for the classes providing custom marshallers.
 * <p/>
 * The marshallers are registered in the {@link AbstractMarshaller#register()} method.
 */
abstract class AbstractMarshaller {

    @Autowired SecurityService modelCatalogueSecurityService
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

    protected getAvailableReports(el) {
        def reports = []

        for (ReportDescriptor descriptor in reportsRegistry.getAvailableReports(el)) {
            if (modelCatalogueSecurityService.userLoggedIn) {
                // for users logged in render all links
                reports << [title: descriptor.getTitle(el) ?: "Generic Report", url: descriptor.getLink(el), type: descriptor.renderType.toString()]
            } else if (descriptor.renderType != ReportDescriptor.RenderType.ASSET) {
                // for users not logged in only let non-asset reports to render
                reports << [title: descriptor.getTitle(el) ?: "Generic Report", url: descriptor.getLink(el), type: descriptor.renderType.toString()]
            }
        }

        reports
    }

}
