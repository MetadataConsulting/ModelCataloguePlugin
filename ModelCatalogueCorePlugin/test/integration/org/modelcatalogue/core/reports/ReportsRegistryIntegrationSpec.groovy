package org.modelcatalogue.core.reports

import grails.rest.render.RenderContext
import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.Relationships
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer
import spock.lang.Stepwise

/**
 * Created by ladin on 09.06.14.
 */
@Stepwise
class ReportsRegistryIntegrationSpec extends IntegrationSpec {

    def "returns reports for the given element"() {
        Model model                 = new Model(name: "Test")
        model.id                    = 1

        ReportsRegistry registry    = applicationContext.getBean(ReportsRegistry)

        registry.register {
            title 'COSD'
            type Model
            link controller: 'model', action: 'index', params: [format: 'xslt'], id: true

        }

        registry.register {
            title 'BLAH'
            type DataElement
            link controller: 'data', action: 'index', params: [format: 'xslt'], id: true
        }

        registry.register {
            title 'WRAPPER'
            item Model
            link controller: 'foo', action: 'bar', params: [format: 'xml']
        }

        def modelReports = registry.getAvailableReports(model)

        expect:
        modelReports.size()                 >= 1
        modelReports[0].title               == 'COSD'
        modelReports[0].getLink(model)      == '/model/index/1?format=xslt'

        when:
        def models = new Elements(itemType: Model)
        def wrapperReports = registry.getAvailableReports(models)

        then:
        wrapperReports.size()               >= 2
        wrapperReports[1].title             == 'WRAPPER'
        wrapperReports[1].getLink(models)   == '/foo/bar?format=xml'

    }


    def "XLSX reports are registered automatically"() {
        ReportsRegistry registry = applicationContext.getBean(ReportsRegistry)
        XLSXListRenderer xlsxListRenderer = applicationContext.getBean(XLSXListRenderer)

        xlsxListRenderer.registerRowWriter {
            title "Export Relationships TEST"
            headers 'Type', 'Source', 'Destination'
            when { ListWrapper container, RenderContext context ->
                Relationship.isAssignableFrom(container.itemType)
            } then { Relationship rel ->
                [[rel.relationshipType.name, rel.source.name, rel.destination.name]]
            }
        }

        def relationshipsReports = registry.getAvailableReports(new Relationships())

        expect:
        relationshipsReports.size()                           >= 2
        relationshipsReports[1].title                         == 'Export Relationships TEST'
        relationshipsReports[1].getLink(new Relationships())  == '?format=xslt&report='



    }
}
