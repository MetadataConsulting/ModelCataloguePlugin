package org.modelcatalogue.core.reports

import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.springframework.beans.factory.annotation.Autowired

@Log4j
class ReportDescriptorRegistry {

    @Autowired LinkGenerator linkGenerator

    List<ReportDescriptor> descriptors = []


    ReportDescriptor register(@DelegatesTo(ReportDescriptorBuilder) Closure definition) {
        ReportDescriptorBuilder builder = new ReportDescriptorBuilder()
        builder.with definition
        ReportDescriptor descriptor = builder.build(linkGenerator)
        descriptors << descriptor
        descriptor
    }


    List<ReportDescriptor> getAvailableReportDescriptors(Object catalogueElement) {
        List<ReportDescriptor> reportDescriptors = []
        for (descriptor in descriptors) {
            try {
                if (descriptor.appliesTo(catalogueElement)) {
                    reportDescriptors << descriptor
                }
            } catch (Exception e) {
                log.error("Error evaluating report: $descriptor.title", e)
            }

        }
        reportDescriptors
    }

}
