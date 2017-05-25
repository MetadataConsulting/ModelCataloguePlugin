package org.modelcatalogue.core.xml.render

import com.google.common.base.Function
import com.google.common.collect.Iterables
import grails.rest.render.AbstractRenderer
import grails.rest.render.RenderContext
import org.codehaus.groovy.grails.web.mime.MimeType
import org.grails.plugins.web.rest.render.ServletRenderContext
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.util.lists.Relationships
import org.modelcatalogue.core.xml.CatalogueXmlPrinter
import org.springframework.beans.factory.annotation.Autowired

/** Renders relationships as XML using CatalogueXmlPrinter */
class RelationshipsXmlRenderer extends AbstractRenderer<Relationships> {

    @Autowired DataModelService dataModelService
    @Autowired DataClassService dataClassService

    RelationshipsXmlRenderer() {
        super(Relationships, MimeType.XML)
    }

    @Override
    void render(Relationships relationships, RenderContext context) {
        Iterable<CatalogueElement> elements = Iterables.transform(relationships.items, { Relationship rel ->
            relationships.direction.getRelation(relationships.owner, rel)
        } as Function<Relationship, CatalogueElement>)

        CatalogueXmlPrinter printer = new CatalogueXmlPrinter(dataModelService, dataClassService)

        if (context instanceof ServletRenderContext) {
            String fileName = "${relationships.owner.name}_${relationships.direction.actionName}_${relationships.type.name}.mc.xml".replaceAll(/\s/, '_')

            context.webRequest.response.contentType = MimeType.XML.name
            context.webRequest.response.setHeader("Content-disposition", "attachment; filename=\"${fileName}\"")
        }

        printer.bind(elements).writeTo(context.writer)
    }
}
