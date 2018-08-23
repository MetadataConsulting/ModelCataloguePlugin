package org.modelcatalogue.gel.export

import static org.modelcatalogue.gel.export.GridReportXlsxStyles.*
import com.google.common.collect.ImmutableMap
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.ValidationRule
import org.modelcatalogue.core.util.DataModelFilter
import builders.dsl.spreadsheet.builder.api.RowDefinition
import builders.dsl.spreadsheet.builder.api.SheetDefinition
import builders.dsl.spreadsheet.builder.api.SpreadsheetBuilder
import builders.dsl.spreadsheet.builder.poi.PoiSpreadsheetBuilder

/**
 * GridReportXlsxExporter.groovy
 * Purpose: Generate an excel report from a data model, including metadata using the required format
 *
 * @author Adam Milward
 * @version 31/03/2017
 */
@CompileStatic
class GridReportXlsxExporter  {

    final CatalogueElement element
    final DataClassService dataClassService
    final GrailsApplication grailsApplication
    final int depth

    static GridReportXlsxExporter create(DataModel element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3) {
        return new GridReportXlsxExporter(element, dataClassService, grailsApplication,  depth)
    }

    GridReportXlsxExporter(CatalogueElement element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3){
        this.element = element
        this.dataClassService = dataClassService
        this.grailsApplication = grailsApplication
        this.depth = depth
    }

    List<DataClass> getDataClasses() {
        return getDataClassesFromModel(element as DataModel)
    }
    List<DataClass> getDataClassesFromModel(DataModel dataModel) {
        return dataClassService.getTopLevelDataClasses(DataModelFilter.includes(dataModel), ImmutableMap.of('status', 'active'), true).items
    }
    protected List<String> excelHeaders = ['Data Element', 'Multiplicity', 'Data Type', 'Validation Rule', 'Business Rule']


    void export(OutputStream outputStream) {
        SpreadsheetBuilder builder = PoiSpreadsheetBuilder.create(outputStream)
        List<DataClass> dataClasses = Collections.emptyList()
        dataClasses = getDataClasses()

        builder.build {
            apply GridReportXlsxStyles
            sheet("$element.name $element.dataModelSemanticVersion" ) { SheetDefinition sheetDefinition ->
                row {
                    style H1
                    cell {
                        value 'Class Hierarchy'
                        colspan depth
                    }
                    excelHeaders.each { header ->
                        cell {
                            value header
                            width auto
                        }
                    }
                }
                dataClasses.each { dataClass->
                    debugLine("GridReportXlsxExporter.export() dataClass: ${dataClass.name}")
                    def childRels = dataClass.getOutgoingRelationshipsByType(RelationshipType.hierarchyType)
                    printClass(dataClass, sheetDefinition, 1, 2, childRels.size(), [])
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
    private Integer buildRows(SheetDefinition sheet, Collection<Relationship> children, int columnDepth, int rowDepth, List outline = []) {
        if (columnDepth > depth) {
            return rowDepth
        }
        children.each { Relationship relationship ->
            CatalogueElement child = relationship.destination
            rowDepth = printClass(child as DataClass, sheet, columnDepth, rowDepth, children.size(), outline)
            outline.removeElement(columnDepth)
        }
        rowDepth
    }

    @CompileDynamic
    private Integer printClass(DataClass child, SheetDefinition sheet, int columnDepth, int rowDepth, int childrenSize, List outline = []) {

        Collection<Relationship> dataElements = child.getOutgoingRelationshipsByType(RelationshipType.containmentType)
        sheet.with { SheetDefinition sheetDefinition ->
            row(rowDepth) { RowDefinition rowDefinition ->
                (1..depth).each{
                    if (it == columnDepth) {
                        cell(columnDepth) {
                            value child.name
                            link to url "${child.defaultModelCatalogueId.split("/catalogue")[0] + "/load?" + child.defaultModelCatalogueId}"
                            style TOP_LEFT_BORDER
                        }
                        outline.add(it)
                    } else if (!outline.contains(it)){
                        cell(it){
                            style TOP_BORDER
                        }
                    }
                }
                if (dataElements) {
                    printDataElement(rowDefinition, dataElements.head(), outline)
                } else {
                    outline.each {
                        cell(it) {
                            style LEFT_BORDER
                        }
                    }
                }
            }
            if (dataElements.size() > 1) {
                for (Relationship dataElementRelationship in dataElements.tail()) {
                    rowDepth++
                    row(rowDepth) { RowDefinition rowDefinition ->
                        printDataElement(rowDefinition, dataElementRelationship, outline)
                    }
                }
            }
            rowDepth = buildRows(sheetDefinition, child.getOutgoingRelationshipsByType(RelationshipType.hierarchyType), columnDepth + 1, (dataElements.size() > 1)?(rowDepth + 1):rowDepth, outline)
            rowDepth
        }
    }

    @CompileDynamic
    void printDataElement(RowDefinition rowDefinition, Relationship dataElementRelationship, List outline = []) {
        DataElement dataElement = dataElementRelationship.destination as DataElement
        Collection<Relationship> relatedTo = dataElement.getRelationshipsByType(RelationshipType.relatedToType)
        if (relatedTo.empty && dataElement?.dataType)
            relatedTo = dataElement?.dataType.getRelationshipsByType(RelationshipType.relatedToType)
        debugLine("GridReportXlsxExporter.printDataElement() dataElement: ${dataElement.name}")
        rowDefinition.with {

            outline.each {
                cell(it){
                    style LEFT_BORDER
                }
            }

            cell(depth + 1) {
                value dataElement.name
                link to url "${getLoadURL(dataElement)}"
                style TOP_LEFT_BORDER
            }

            [ "${getMultiplicity(dataElementRelationship)}",
              "${(dataElement?.dataType) ? printDataType(dataElement?.dataType) : ""}",
              "${(dataElement?.dataType?.rule) ? dataElement?.dataType?.rule : ""}",
              "${(dataElement?.involvedIn) ? printBusRule(dataElement?.involvedIn) : ""}"].each { cellValue ->
                cell {
                    value cellValue
                    style STANDARD
                }
            }

        }
    }

    String printDataType(DataType dataType) {

        if (dataType.instanceOf(EnumeratedType)) {
            return (dataType as EnumeratedType).prettyPrint()
        }

        return dataType.name
    }

    String getMultiplicity(Relationship dataElementRelationship) {
        String multiplicityText = " "

        if (dataElementRelationship?.ext.get("Min Occurs")=="0") {
            multiplicityText += "Optional "
        } else if (dataElementRelationship?.ext.get("Min Occurs")=="1") {
            multiplicityText += "Mandatory "
        }

        if (dataElementRelationship?.ext.get("Max Occurs")=="*") {
            multiplicityText += "Multiple "
        }

        if (dataElementRelationship.source.ext.get("http://xsd.modelcatalogue.org/section#type")=="choice") {
            multiplicityText += " CHOICE "
        }

        if (dataElementRelationship?.ext.get("Max Occurs") && dataElementRelationship?.ext.get("Min Occurs")) {
            multiplicityText += "(" + dataElementRelationship?.ext.get("Min Occurs") + ".." + dataElementRelationship?.ext.get("Max Occurs") + ")"
        }
        multiplicityText
    }

    String printBusRule(List<ValidationRule> rules){
        return rules.collect{ it.name }.join('\n')
    }
    String getLoadURL(CatalogueElement ce){
        ce?.defaultModelCatalogueId.split("/catalogue")[0] + "/load?" + ce.defaultModelCatalogueId
    }
}

