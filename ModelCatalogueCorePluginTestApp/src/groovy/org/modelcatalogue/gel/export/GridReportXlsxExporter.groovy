package org.modelcatalogue.gel.export

import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.H1
import com.google.common.collect.ImmutableMap
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

    protected Closure standardCellStyle = {
        wrap text
        border top, {
            color black
            style medium
        }
    }
    List<DataClass> getDataClasses() {
        return getDataClassesFromModel(element as DataModel)
    }
    List<DataClass> getDataClassesFromModel(DataModel dataModel) {
        return dataClassService.getTopLevelDataClasses(DataModelFilter.includes(dataModel), ImmutableMap.of('status', 'active'), true).items
    }
    protected List<String> excelHeaders = ['Data Element', 'Multiplicity', 'Data Type', 'Validation Rule', 'Business Rule', 'Labkey Field Name', 'Labkey View', 'Additional review', 'Additional Rule']

    Map<String, Closure> sheetsAfterMainSheetExport() {}

    void export(OutputStream outputStream) {
        SpreadsheetBuilder builder = new PoiSpreadsheetBuilder()
        List<DataClass> dataClasses = Collections.emptyList()
        dataClasses = getDataClasses()

        builder.build(outputStream) {
            apply ModelCatalogueStyles
            sheet("$element.name $element.dataModelSemanticVersion" ) { SheetDefinition sheetDefinition ->
                row {
                    cell {
                        value 'Class Hierarchy'
                        colspan depth
                        style H1
                    }

                    excelHeaders.each{ header ->
                        cell {
                            value header
                            width auto
                            style H1
                        }
                    }
                }
                dataClasses.each{ dataClass->
                    buildRows(sheetDefinition, dataClass.getOutgoingRelationshipsByType(RelationshipType.hierarchyType), 1, 2)
                }

            }
            sheetsAfterMainSheetExport().each{name, instructions ->
                sheet(name, instructions)
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
            rowDepth = printClass(child, sheet, columnDepth, rowDepth, children.size(), outline)
            outline.removeElement(columnDepth)
        }
        rowDepth
    }


    private Integer printClass(DataClass child, SheetDefinition sheet, int columnDepth, int rowDepth, int childrenSize, List outline = []) {

        Collection<Relationship> dataElements = child.getOutgoingRelationshipsByType(RelationshipType.containmentType)
        sheet.with { SheetDefinition sheetDefinition ->
            row(rowDepth) { RowDefinition rowDefinition ->
                (1..depth).each{
                    if(it==columnDepth){
                        cell(columnDepth) {
                            value child.name
                            link to url "${child.defaultModelCatalogueId.split("/catalogue")[0] + "/load?" + child.defaultModelCatalogueId}"
                            style {
                                wrap text
                                border top, left, {
                                    color black
                                    style medium
                                }
                            }
                        }
                        outline.add(it)
                    }else if (!outline.contains(it)){
                       cell(it){
                           style {
                               wrap text
                               border top, {
                                   color black
                                   style medium
                               }
                           }
                       }

                    }
                }
                if (dataElements) {
                    printDataElement(rowDefinition, dataElements.head(), outline)
                }else{
                    outline.each {
                            cell(it) {
                                style {
                                    wrap text
                                    border left, {
                                        color black
                                        style medium
                                    }
                                }
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

    void printDataElement(RowDefinition rowDefinition, Relationship dataElementRelationship, List outline = []) {
        DataElement dataElement = dataElementRelationship.destination
        Collection<Relationship> relatedTo = dataElement.getRelationshipsByType(RelationshipType.relatedToType)
        if(relatedTo.empty && dataElement?.dataType) relatedTo = dataElement?.dataType.getRelationshipsByType(RelationshipType.relatedToType)
        rowDefinition.with {

                outline.each{

                    cell(it){
                        style {
                            wrap text
                            border left, {
                                color black
                                style medium
                            }
                        }
                    }

                }

                cell(depth + 1) {
                    value dataElement.name
                    link to url "${getLoadURL(dataElement)}"
                    style {
                        wrap text
                        border top, left, {
                            color black
                            style medium
                        }
                    }
                }

                [ "${getMultiplicity(dataElementRelationship)}",
                  "${(dataElement?.dataType) ? printDataType(dataElement?.dataType) : ""}",
                  "${(dataElement?.dataType?.rule) ? dataElement?.dataType?.rule : ""}",
                  "${(dataElement?.involvedIn) ? printBusRule(dataElement?.involvedIn) : ""}",
                  "${(dataElement?.ext.get("LabKey Field Name")) ?: ""}",
                  "${(dataElement?.ext.get("Additional Review")) ?: ""}",
                  "${(dataElement?.ext.get("Additional Rule")) ?: ""}",
                  "${(dataElement?.ext.get("Additional Rule Dependency")) ?: ""}"].
                    each{cellValue ->
                        cell {
                            value cellValue
                            style standardCellStyle
                        }

                    }

            }
        }



    String printDataType(DataType dataType){

        if(dataType.instanceOf(EnumeratedType)){
            return dataType.prettyPrint()
        }

        return dataType.name

    }

    String getMultiplicity(Relationship dataElementRelationship){
        String multiplicityText = " "

        if(dataElementRelationship?.ext.get("Min Occurs")=="0"){
            multiplicityText += "Optional "
        }else if(dataElementRelationship?.ext.get("Min Occurs")=="1"){
            multiplicityText += "Mandatory "
        }

        if(dataElementRelationship?.ext.get("Max Occurs")=="*"){
            multiplicityText += "Multiple "
        }

        if(dataElementRelationship.source.ext.get("http://xsd.modelcatalogue.org/section#type")=="choice"){
            multiplicityText += " CHOICE "
        }

        if(dataElementRelationship?.ext.get("Max Occurs") && dataElementRelationship?.ext.get("Min Occurs")){
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
