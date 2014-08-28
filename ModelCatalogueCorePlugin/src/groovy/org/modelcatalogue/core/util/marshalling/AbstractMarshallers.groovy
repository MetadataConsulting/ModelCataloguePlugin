package org.modelcatalogue.core.util.marshalling

import grails.converters.JSON
import grails.converters.XML
import org.modelcatalogue.core.SecurityService
import org.modelcatalogue.core.reports.ReportDescriptor
import org.springframework.beans.factory.annotation.Autowired

/**
 * Interface for the classes providing custom marshallers.
 * <p/>
 * The marshallers are registered in the {@link AbstractMarshallers#register()} method.
 */
abstract class AbstractMarshallers {

    @Autowired SecurityService modelCatalogueSecurityService

    final Class type

    AbstractMarshallers(Class type) {
        this.type = type
    }

    final void register() {
        JSON.registerObjectMarshaller(type) { el ->
            if (!el) return null
            prepareJsonMap(el)
        }

        Closure cl ={ el, XML xml ->
            if (!el) return
            addXmlAttributes(el, xml)
            buildXml(el, xml)
        }


        if (supportingCustomElementName) {
            XML.registerObjectMarshaller(new NameAwareClosureObjectMarshaller<XML>({ getElementName(it) }, type, cl))
        } else {
            XML.registerObjectMarshaller(type, cl)
        }
    }

    abstract protected Map<String, Object> prepareJsonMap(element)

    protected void buildXml(element, XML xml) {}

    protected void addXmlAttributes(element, XML xml) {}

    protected static void addXmlAttribute(property, String attribute, XML xml) {
        if(property!=null){xml.attribute(attribute, "${property}")}
    }

    protected String getElementName(element) { return null }
    protected boolean isSupportingCustomElementName() { return false }

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
