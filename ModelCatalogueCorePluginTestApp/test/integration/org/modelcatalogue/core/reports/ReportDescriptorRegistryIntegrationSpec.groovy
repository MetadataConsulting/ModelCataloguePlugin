package org.modelcatalogue.core.reports

import grails.rest.render.RenderContext
import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.util.lists.Elements
import org.modelcatalogue.core.util.lists.ListWrapper
import org.modelcatalogue.core.util.lists.Relationships
import org.modelcatalogue.core.util.lists.SimpleListWrapper
import spock.lang.Stepwise

@Stepwise
class ReportDescriptorRegistryIntegrationSpec extends IntegrationSpec {

    def "returns reports for the given element"() {
        DataClass model                 = new DataClass(name: "Test")
        model.id                    = 1

        LinkGenerator linkGenerator = applicationContext.getBean(LinkGenerator)
        ReportDescriptorRegistry registry    = new ReportDescriptorRegistry(linkGenerator: linkGenerator)

        registry.register {
            title 'Export All to COSD'
            type DataClass
            link controller: 'dataArchitect', action: 'getSubModelElements', params: [format: 'xslt', report: 'COSD'], id: true

        }

        registry.register {
            creates link
            title 'BLAH'
            type DataElement
            link controller: 'data', action: 'index', params: [format: 'xslt'], id: true
        }

        registry.register {
            creates asset
            title 'WRAPPER'
            item DataClass
            link controller: 'foo', action: 'bar', params: [format: 'xml']
        }

        def modelReports = registry.getAvailableReportDescriptors(model)

        expect:
        modelReports.size()                 >= 1
        modelReports[0].getTitle(model)     == 'Export All to COSD'
        modelReports[0].getLink(model)?.endsWith("/api/modelCatalogue/core/dataArchitect/getSubModelElements?format=xslt&report=COSD&id=1")

        when:
        def models = new Elements(itemType: DataClass)
        def wrapperReports = registry.getAvailableReportDescriptors(models)

        then:
        wrapperReports.size()               >= 1
        wrapperReports[0].getTitle(models)  == 'WRAPPER'
        wrapperReports[0].getLink(models)?.endsWith('/foo/bar?format=xml&asset=true')

    }
}
