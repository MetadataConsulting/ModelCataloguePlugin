package org.modelcatalogue.prsb.export

import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.H1
import com.google.common.collect.ImmutableMap
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.*
import org.modelcatalogue.core.export.inventory.ModelCatalogueStyles
import org.modelcatalogue.core.util.DataModelFilter
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
class PRSBGridReportXlsxExporter {

    final CatalogueElement element
    final DataClassService dataClassService
    final GrailsApplication grailsApplication
    final int depth

    static PRSBGridReportXlsxExporter create(DataModel element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3) {
        return new PRSBGridReportXlsxExporter(element, dataClassService, grailsApplication,  depth)
    }


    private PRSBGridReportXlsxExporter(CatalogueElement element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3){
        this.element = element
        this.dataClassService = dataClassService
        this.grailsApplication = grailsApplication
        this.depth = depth
    }

    void export(OutputStream outputStream) {
        SpreadsheetBuilder builder = new PoiSpreadsheetBuilder()
        List<DataClass> dataClasses = Collections.emptyList()
        dataClasses = dataClassService.getTopLevelDataClasses(DataModelFilter.includes(element as DataModel), ImmutableMap.of('status', 'active'), true).items

        builder.build(outputStream) {
            apply ModelCatalogueStyles
            sheet("$element.name $element.dataModelSemanticVersion" ) { SheetDefinition sheetDefinition ->
                row {
                    cell {
                        value 'Class Hierarchy'
                        colspan depth
                        style H1
                    }
                    cell {
                        value 'Data Element'
                        width auto
                        style H1
                    }

                    cell {
                        value 'Matched'
                        width auto
                        style H1
                    }
                    cell {
                        value '% Match'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Matched Name'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Matched On'
                        width auto
                        style H1
                    }


                }

                dataClasses.each{ dataClass->
                    buildRows(sheetDefinition, dataClass.getOutgoingRelationshipsByType(RelationshipType.hierarchyType), 1, 2)
                }

            }
        }

    }

    /**
     * Renders rows for each data class passed in children collection.
     * @param sheet the current sheet
     * @param children data classes to be rendered
     * @param currentDepth the current depth starting with one
     */
    private Integer buildRows(SheetDefinition sheet, Collection<Relationship> children, int columnDepth, int rowDepth) {
        if (columnDepth > depth) {
            return rowDepth
        }
        children.each { Relationship relationship ->
            CatalogueElement child = relationship.destination
            rowDepth = printClass(child, sheet, columnDepth, rowDepth, children.size())
        }
        rowDepth
    }


    private Integer printClass(DataClass child, SheetDefinition sheet, int columnDepth, int rowDepth, int childrenSize) {

        Collection<Relationship> dataElements = child.getOutgoingRelationshipsByType(RelationshipType.containmentType)
        sheet.with { SheetDefinition sheetDefinition ->
            row(rowDepth) { RowDefinition rowDefinition ->
                cell(columnDepth) {
                    value child.name
                    link to url "${child.defaultModelCatalogueId.split("/catalogue")[0] + "/load?" + child.defaultModelCatalogueId}"
                }
                if (dataElements) {
                    printDataElement(rowDefinition, dataElements.head())
                }
            }
            if (dataElements.size() > 1) {
                for (Relationship dataElementRelationship in dataElements.tail()) {
                    rowDepth++
                    row(rowDepth) { RowDefinition rowDefinition ->
                        printDataElement(rowDefinition, dataElementRelationship)
                    }
                }
            }
            rowDepth = buildRows(sheetDefinition, child.getOutgoingRelationshipsByType(RelationshipType.hierarchyType), columnDepth + 1, (childrenSize > 1 || dataElements.size() > 1) ? (rowDepth + 1) : rowDepth)
            rowDepth
        }
    }

    void printDataElement(RowDefinition rowDefinition, Relationship dataElementRelationship) {
        DataElement dataElement = dataElementRelationship.destination
        Collection<Relationship> relatedTo = dataElement.getRelationshipsByType(RelationshipType.relatedToType)
        if(relatedTo.empty && dataElement?.dataType) relatedTo = dataElement?.dataType.getRelationshipsByType(RelationshipType.relatedToType)
        rowDefinition.with {
            cell(depth + 1) {
                value dataElement.name
                link to url "${dataElement.defaultModelCatalogueId.split("/catalogue")[0] + "/load?" + dataElement.defaultModelCatalogueId}"
            }

            if(relatedTo && relatedTo.head()?.destination?.dataModel?.id == 68111){
                cell{
                    value "MATCHED"
                }
                printMatches(rowDefinition, relatedTo.head())
            }else{
                cell{
                    value "NO MATCH"
                }
            }


        }
    }

    void printMatches(RowDefinition rowDefinition, Relationship dataElementRelationship) {
        CatalogueElement element = dataElementRelationship.destination
        rowDefinition.with {
            cell(depth + 3) {
                value "${dataElementRelationship.ext.get("match") ?: 'NA'}"
            }
            cell {
                value "$element.name ($element.dataModel.name)"
                link to url "${element.defaultModelCatalogueId.split("/catalogue")[0] + "/load?" + element.defaultModelCatalogueId}"
            }
            cell {
                value "${dataElementRelationship.ext.get("matchOn") ?: 'MANUAL'}"
            }

        }
    }



    String printDataType(DataType dataType){

        if(dataType.instanceOf(EnumeratedType)){
            return dataType.prettyPrint()
        }

        return dataType.name

    }

}
