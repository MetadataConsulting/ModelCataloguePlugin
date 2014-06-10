package org.modelcatalogue.core.reports

import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by ladin on 09.06.14.
 */
class ReportsRegistry {

    @Autowired LinkGenerator linkGenerator

    List<ReportDescriptor> descriptors = []


    ReportDescriptor register(@DelegatesTo(ReportDescriptorBuilder) Closure definition) {
        ReportDescriptorBuilder builder = new ReportDescriptorBuilder()
        builder.with definition
        ReportDescriptor descriptor = builder.build(linkGenerator)
        descriptors << descriptor
        descriptor
    }


    List<ReportDescriptor> getAvailableReports(Object model) {
        List<ReportDescriptor> reports = []
        for (descriptor in descriptors) {
            if (descriptor.appliesTo(model)) {
                reports << descriptor
            }
        }
        reports
    }

}
