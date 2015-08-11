package org.modelcatalogue.core.util.marshalling.xlsx

import grails.rest.render.RenderContext
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.Relationships
import spock.lang.Specification

class XLSXRendererSpec extends Specification {

    def "register and find row writers"() {
        RenderContext showContext = Mock(RenderContext)
        showContext.getActionName() >> 'show'

        RenderContext indexContext = Mock(RenderContext)
        indexContext.getActionName() >> 'index'

        XLSXListRenderer renderer = new XLSXListRenderer(reportsRegistry: new ReportsRegistry())

        XLSXRowWriter idNameDescWriter = renderer.registerRowWriter {
            headers 'ID', 'Name', 'Description'
            append metadata
            when { ListWrapper container, RenderContext context ->
                context.actionName in ['index', 'search'] && CatalogueElement.isAssignableFrom(container.itemType)
            } then { CatalogueElement element ->
                [[element.id, element.name, element.description]]
            }
        }

        XLSXRowWriter reversedWriter = renderer.registerRowWriter('reversed') {
            headers 'Description', 'Name', 'ID'
            when { ListWrapper container, RenderContext context ->
                context.actionName in ['index', 'search'] && CatalogueElement.isAssignableFrom(container.itemType)
            } then { CatalogueElement element ->
                [[element.id, element.name, element.description]]
            }
        }

        XLSXRowWriter relationshipWriter = renderer.registerRowWriter {
            headers 'Type', 'Source', 'Destination'
            when { container, context ->
                container instanceof Relationships
            } then { Relationship r ->
                [[r.relationshipType, r.source.name, r.destination.name]]
            }
        }

        when:
        def w1 = renderer.findRowWriter(null, new Elements(itemType: DataType), showContext)
        def w2 = renderer.findRowWriter(null, new Elements(itemType: DataType), indexContext)
        def w3 = renderer.findRowWriter('reversed', new Elements(itemType: DataType), indexContext)
        def w4 = renderer.findRowWriter(null, new Relationships(), indexContext)

        then:
        w1 == XLSXListRenderer.DEFAULT_WRITER
        w2 == idNameDescWriter
        w3 == reversedWriter
        w4 == relationshipWriter

    }

}