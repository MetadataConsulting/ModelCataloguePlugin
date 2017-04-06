package org.modelcatalogue.gel.export

import com.google.common.collect.ImmutableMap
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.diff.CatalogueElementDiffs
import org.modelcatalogue.core.export.inventory.CatalogueElementToXlsxExporter
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.spreadsheet.builder.api.RowDefinition
import org.modelcatalogue.spreadsheet.builder.api.SheetDefinition
import org.modelcatalogue.spreadsheet.builder.api.SpreadsheetBuilder
import org.modelcatalogue.spreadsheet.builder.poi.PoiSpreadsheetBuilder


/**
 * GridReportXlsxExporter.groovy
 * Purpose: Generate an excel report from a data model, including metadata using the required format
 *
 * @author Adam Milward
 * @version 31/03/2017
 */
class GridReportXlsxExporter  {

    final CatalogueElement element
    final DataClassService dataClassService
    final GrailsApplication grailsApplication
    final int depth

    static GridReportXlsxExporter create(DataClass element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3) {
        return new GridReportXlsxExporter(element, dataClassService, grailsApplication,  depth)
    }


    private GridReportXlsxExporter(CatalogueElement element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3){
        this.element = element
        this.dataClassService = dataClassService
        this.grailsApplication = grailsApplication
        this.depth = depth
    }

    void export(OutputStream outputStream) {
        SpreadsheetBuilder builder = new PoiSpreadsheetBuilder()

        builder.build(outputStream) {
            sheet("$element.name $element.dataModelSemanticVersion" ) { SheetDefinition sheetDefinition ->
                row {
                    cell {
                        value 'Hierarchy Levels'
                        colspan depth
                    }
                    cell {
                        value 'Data Fields'
                        colspan 2
                        width auto
                    }
                    cell {
                        value 'Data type'
                        width auto
                    }
                    cell {
                        value 'Labkey field name'
                        width auto
                    }
                    cell {
                        value 'Labkey view'
                        width auto
                    }
                    cell {
                        value 'Mandatory / Conditional'
                        width auto
                    }
                    cell {
                        value 'Validation rule'
                        width auto
                    }
                    cell {
                        value 'Validation rule dependency link'
                        width auto
                    }
                    cell {
                        value 'Additional review'
                        width auto
                    }
                }

                row { RowDefinition rowDefinition ->
                    (1..depth).each { Integer i ->
                        rowDefinition.cell {
                            value i
                            width auto
                        }
                    }
                }

                buildRows(sheetDefinition, element.getOutgoingRelationshipsByType(RelationshipType.hierarchyType), 1)
            }
        }

    }

    /**
     * Renders rows for each data class passed in children collection.
     * @param sheet the current sheet
     * @param children data classes to be rendered
     * @param currentDepth the current depth starting with one
     */
    private void buildRows(SheetDefinition sheet, Collection<Relationship> children, int currentDepth) {
        if (currentDepth > depth) {
            return
        }
        children.each { Relationship relationship ->
            CatalogueElement child = relationship.destination
            Collection<Relationship> dataElements = child.getOutgoingRelationshipsByType(RelationshipType.containmentType)
            sheet.with { SheetDefinition sheetDefinition ->
                row { RowDefinition rowDefinition ->
                    cell(currentDepth) {
                        value child.name
                    }
                    if (dataElements) {
                        printDataElement(rowDefinition, dataElements.head())
                    }
                }
                if (dataElements.size() > 1) {
                    for (Relationship dataElementRelationship in dataElements.tail()) {
                        row { RowDefinition rowDefinition ->
                            printDataElement(rowDefinition, dataElementRelationship)
                        }
                    }
                }
                buildRows(sheetDefinition, child.getOutgoingRelationshipsByType(RelationshipType.hierarchyType), currentDepth + 1)
            }
        }
    }

    void printDataElement(RowDefinition rowDefinition, Relationship dataElementRelationship) {
        CatalogueElement dataElement = dataElementRelationship.destination
        rowDefinition.with {
            cell(depth + 1) {
                value dataElement.name
            }
            cell {
                value "${dataElementRelationship.ext[Metadata.MIN_OCCURS] ?: '0'}..${dataElementRelationship.ext[Metadata.MAX_OCCURS] ?: '*'}"
            }
        }
    }

}
