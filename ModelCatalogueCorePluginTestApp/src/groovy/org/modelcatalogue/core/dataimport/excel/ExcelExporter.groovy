package org.modelcatalogue.core.dataimport.excel

import com.google.common.collect.ImmutableMap
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.*
import org.modelcatalogue.core.export.inventory.ModelCatalogueStyles
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.spreadsheet.builder.api.RowDefinition
import org.modelcatalogue.spreadsheet.builder.api.SheetDefinition
import org.modelcatalogue.spreadsheet.builder.api.SpreadsheetBuilder
import org.modelcatalogue.spreadsheet.builder.poi.PoiSpreadsheetBuilder

import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.H1

/**
 * Excel.groovy
 * Purpose: Generate an excel report from a data model, including metadata using the required format
 *
 * @author Adam Milward
 * @version 31/03/2017
 */
class ExcelExporter {

    final CatalogueElement element
    final DataClassService dataClassService
    final GrailsApplication grailsApplication
    final int depth

    static ExcelExporter create(DataModel element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3) {
        return new ExcelExporter(element, dataClassService, grailsApplication,  depth)
    }


    ExcelExporter(CatalogueElement element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3){
        this.element = element
        this.dataClassService = dataClassService
        this.grailsApplication = grailsApplication
        this.depth = depth
    }

    protected Closure standardCellStyle = {
        wrap text
        border top, left, {
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
    protected List<String> excelHeaders = [
            'Parent Data Class ID','Parent Data Class Name',
            'Data Class ID', 'Data Class Name',
            'Data Element ID', 'Data Element Name',
            'Multiplicity', 'Data Element Description',
            'Data Type ID', 'Data Type Name', 'Data Type Enumerations', 'Data Type Rule',
            'Measurement Unit ID', 'Measurement Unit Name',
            'Metadata']

    Map<String, Closure> sheetsAfterMainSheetExport() {}

    void export(OutputStream outputStream) {
        SpreadsheetBuilder builder = new PoiSpreadsheetBuilder()
        List<DataClass> dataClasses = Collections.emptyList()
        dataClasses = getDataClasses()

        builder.build(outputStream) {
            apply ModelCatalogueStyles
            sheet("$element.name $element.dataModelSemanticVersion" ) { SheetDefinition sheetDefinition ->
                row {
                    excelHeaders.each{ header ->
                        cell {
                            value header
                            width auto
                            style H1
                        }
                    }
                }
                dataClasses.each{ dataClass->
                    buildRows(sheetDefinition, dataClass, 2)
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
    private Integer buildRows(SheetDefinition sheet, DataClass parent, int rowDepth) {

        Collection<Relationship> children = parent.getOutgoingRelationshipsByType(RelationshipType.hierarchyType)
        children.each { Relationship relationship ->
            CatalogueElement child = relationship.destination
            rowDepth = printClass(parent, child, sheet, rowDepth)
        }
        rowDepth
    }


    private Integer printClass(DataClass parent, DataClass child, SheetDefinition sheet, int rowDepth) {
        Collection<Relationship> dataElements = child.getOutgoingRelationshipsByType(RelationshipType.containmentType)
        sheet.with { SheetDefinition sheetDefinition ->
            row { RowDefinition rowDefinition ->
                    for (Relationship dataElementRelationship in dataElements) {

                        row(rowDepth) { RowDefinition rd ->
                            printDataElement(parent, child, rd, dataElementRelationship)
                        }
                        rowDepth++
                    }
                }
            rowDepth = buildRows(sheetDefinition, child, rowDepth)
            rowDepth
            }
        }

    void printDataElement(DataClass parent, DataClass child, RowDefinition rowDefinition, Relationship dataElementRelationship, List outline = []) {
        DataElement dataElement = dataElementRelationship.destination
        Collection<Relationship> relatedTo = dataElement.getRelationshipsByType(RelationshipType.relatedToType)
        if(relatedTo.empty && dataElement?.dataType) {
            relatedTo = dataElement?.dataType.getRelationshipsByType(RelationshipType.relatedToType)}

        String blank = ''

        rowDefinition.with {
            //'Parent Data Class ID','Parent Data Class Name',
            cell {
                value getModelCatalogueIdToPrint(parent)
                link to url "${urlFromModelCatalogueId(parent.defaultModelCatalogueId)}"
                style standardCellStyle
            }
            cell {
                value parent.name
                style standardCellStyle
            }

            //'Data Class ID', 'Data Class Name',
            cell {
                value getModelCatalogueIdToPrint(child)
                link to url "${urlFromModelCatalogueId(child.defaultModelCatalogueId)}"
                style standardCellStyle
            }
            cell {
                value child.name
                style standardCellStyle
            }

            //'Data Element ID', 'Data Element Name',
            cell {
                value getModelCatalogueIdToPrint(dataElement)
                link to url "${urlFromModelCatalogueId(dataElement.defaultModelCatalogueId)}"

                style standardCellStyle
            }
            cell {
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

            //'Multiplicity', 'Data Element Description',
            cell{
                value "${getMultiplicity(dataElementRelationship)}"
                style standardCellStyle
            }
            cell{
                value "${dataElement.description}"
                style standardCellStyle
            }
            // 'Data Type ID', 'Data Type Name',
            cell{
                value "${(dataElement?.dataType) ? getModelCatalogueIdToPrint(dataElement?.dataType) : blank}"
                style standardCellStyle
            }
            cell{
                value "${dataElement?.dataType.name}"
                style standardCellStyle
            }

            //'Data Type Enumerations', 'Data Type Rule',
            cell{
                value "${(dataElement?.dataType) ? printEnumeratedType(dataElement?.dataType) : blank}"
                style standardCellStyle
            }
            cell{
                value "${(dataElement?.dataType?.rule) ?: blank}"
                style standardCellStyle
            }

            //'Measurement Unit ID', 'Measurement Unit Name',
            if (dataElement?.dataType?.instanceOf(PrimitiveType)) {
                PrimitiveType prim = dataElement?.dataType
                cell {
                    value "${getModelCatalogueIdToPrint(prim?.measurementUnit)}"
                    style standardCellStyle
                }
                cell {
                    value "${prim?.measurementUnit.name}"
                    style standardCellStyle
                }
            }
            else {
                // leave blank
                cell {style standardCellStyle}
                cell {style standardCellStyle}
            }
            // 'Metadata'

            cell {
                value "${dataElement?.ext.collect { key, value -> "$key: $value"}.join('\n')}"
                style standardCellStyle
            }


//                [ ,
//                  ,
//                  ,
//                  "${(dataElement?.involvedIn) ? printBusRule(dataElement?.involvedIn) : blank}",
//                  "${(dataElement?.ext.get("LabKey Field Name")) ?: blank}",
//                  "${(dataElement?.ext.get("Additional Review")) ?: blank}",
//                  "${(dataElement?.ext.get("Additional Rule")) ?: blank}",
//                  "${(dataElement?.ext.get("Additional Rule Dependency")) ?: blank}"].
//                    each{cellValue ->
//                        cell {
//                            value cellValue
//                            style standardCellStyle
//                        }
//                    }
            }
        }



    String printEnumeratedType(DataType dataType){

        if(dataType.instanceOf(EnumeratedType)){
            EnumeratedType enumerated = (EnumeratedType) dataType
            return enumerated.prettyPrint()
        }

        return " "

    }

    String getMultiplicity(Relationship dataElementRelationship){
        if (dataElementRelationship) {
            Map<String, String> ext = dataElementRelationship.ext
            String min = ext.get("Min Occurs") ?: '0'
            String max = ext.get("Max Occurs") ?: '*'
            return "$min..$max"
        }
        else return ""
    }

    String printBusRule(List<ValidationRule> rules){
        return rules.collect{ it.name }.join('\n')
    }
    String getLoadURL(CatalogueElement ce){
        ce?.defaultModelCatalogueId.split("/catalogue")[0] + "/load?" + ce.defaultModelCatalogueId
    }

    String urlFromModelCatalogueId(String modelCatalogueId) {
        return modelCatalogueId.split("/catalogue")[0] + "/load?" + modelCatalogueId
    }
    protected static String getModelCatalogueIdToPrint(CatalogueElement element) {
        element.hasModelCatalogueId() && !element.modelCatalogueId.startsWith('http') ? element.modelCatalogueId : element.combinedVersion
    }

}
