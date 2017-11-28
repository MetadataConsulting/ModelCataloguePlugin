package org.modelcatalogue.core.dataimport.excel

import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.H1
import com.google.common.collect.ImmutableMap
import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.*
import org.modelcatalogue.core.export.inventory.ModelCatalogueStyles
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.spreadsheet.builder.api.RowDefinition
import org.modelcatalogue.spreadsheet.builder.api.SheetDefinition
import org.modelcatalogue.spreadsheet.builder.api.SpreadsheetBuilder
import org.modelcatalogue.spreadsheet.builder.poi.PoiSpreadsheetBuilder

/**
 * ExcelExporter.groovy
 * Purpose: Generate an excel report from a data model, including metadata using the required format.
 * We'll try to make this round-trip with ExcelLoader, changing ExcelLoader to match the format defined here.
 *
 * This class was originally copied from the GridReportXlsxExporter class.
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
                dataClasses.each { dataClass->
                    /* The original GridReportXlsxExporter had columnDepth and rowDepth...
                    the depth parameter of the class was used for columnDepth– how far to the right the columns went. rowDepth is how far down, which row is being edited.
                    Init: 2 is where the first row should be printed.
                    */
                    printClass(null, dataClass, sheetDefinition, 2)
                    // starting at printClass lets the top level data class be printed.
                    //buildRows(sheetDefinition, dataClass, 2)
                }

            }
            sheetsAfterMainSheetExport().each{name, instructions ->
                sheet(name, instructions)
            }
        }

    }

    /**
     * Calls printClass for each child of passed in data class.
     * Mutually recursive with printClass.
     * If the parameter rowDepth at the beginning is where the next row should be printed,
     * then the returned value of rowDepth, after all the printing done in the body, is updated so that it is still where the next row should be printed.
     * @param sheet the current sheet
     * @param parentDataClass data class whose children are to be rendered
     * @param currentDepth the current depth starting with one
     */
    @CompileStatic
    private Integer buildRows(SheetDefinition sheet, DataClass parentDataClass, int rowDepth) {

        Collection<Relationship> hierarchyRels = parentDataClass.getOutgoingRelationshipsByType(RelationshipType.hierarchyType)
        Collection<DataClass> childDataClasses = hierarchyRels.collect {rel ->
            (DataClass) rel.destination
        }
        // get DataClass children
        childDataClasses.each { DataClass childDataClass ->
            // [each loop] invariant: rowDepth is where next row should be printed.
            rowDepth = printClass(parentDataClass, childDataClass, sheet, rowDepth)
        }
        rowDepth
    }

    /**
     * Prints a row for each of childDataClass's data elements with info about parentDataClass, childDataClass and the data element;
     * Or a row with info about parentDataClass and childDataClass if there are no data elements.
     * If rowDepth parameter is where the next row should be printed at the start of the method,
     * the returned rowDepth value is where the next row should be printed at the time the method finishes.
     * @param parentDataClass
     * @param childDataClass
     * @param sheet
     * @param rowDepth
     * @return
     */
    private Integer printClass(DataClass parentDataClass, DataClass childDataClass, SheetDefinition sheet, int rowDepth) {
        Collection<Relationship> containmentRels = childDataClass.getOutgoingRelationshipsByType(RelationshipType.containmentType)
        sheet.with { SheetDefinition sheetDefinition ->
            row { RowDefinition rowDefinition ->
                    if (containmentRels.isEmpty()) {
                        // still want to print parentDataClass/childDataClass even if no data elements
                        row(rowDepth) { RowDefinition rd ->
                            printChildProbablyParentPossiblyElement(parentDataClass, childDataClass, rd, null)
                        }
                        rowDepth++
                    }
                    for (Relationship dataElementRelationship in containmentRels) {
                        // [for loop] invariant: rowDepth is where the next row should be printed.
                        row(rowDepth) { RowDefinition rd ->
                            printChildProbablyParentPossiblyElement(parentDataClass, childDataClass, rd, dataElementRelationship)
                        }
                        rowDepth++
                    }
                }

            rowDepth = buildRows(sheetDefinition, childDataClass, rowDepth)
            rowDepth
            }
        }
    String blank = ''

    void printChildProbablyParentPossiblyElement(DataClass parent, DataClass child, RowDefinition rowDefinition, Relationship dataElementRelationship, List outline = []) {

        rowDefinition.with {
            Closure printParent = {
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
            }
            Closure printChild = {
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
            }
            if (parent) {
                printParent()
            }
            else { // no parent
                cell {style standardCellStyle}
                cell {style standardCellStyle}
            }
            printChild()

            if (dataElementRelationship) {

                DataElement dataElement = dataElementRelationship.destination
                Collection<Relationship> relatedTo = dataElement.getRelationshipsByType(RelationshipType.relatedToType)
                if(relatedTo.empty && dataElement?.dataType) {
                    relatedTo = dataElement?.dataType.getRelationshipsByType(RelationshipType.relatedToType)}

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
                    value "${dataElement?.description ?: blank}"
                    style standardCellStyle
                }
                // 'Data Type ID', 'Data Type Name',
                cell{
                    value "${(dataElement?.dataType) ? getModelCatalogueIdToPrint(dataElement?.dataType) : blank}"
                    style standardCellStyle
                }
                cell{
                    value "${dataElement?.dataType?.name ?: blank}"
                    style standardCellStyle
                }

                //'Data Type Enumerations', 'Data Type Rule',
                cell{
                    value "${(dataElement?.dataType) ? printEnumeratedType(dataElement?.dataType) : blank}"
                    style standardCellStyle
                }
                cell{
                    value "${dataElement?.dataType?.rule ?: blank}"
                    style standardCellStyle
                }

                //'Measurement Unit ID', 'Measurement Unit Name',
                if (dataElement?.dataType?.instanceOf(PrimitiveType)) {
                    PrimitiveType prim = (PrimitiveType) dataElement.dataType
                    cell {
                        value "${getModelCatalogueIdToPrint(prim?.measurementUnit)}"
                        style standardCellStyle
                    }
                    cell {
                        value "${prim.measurementUnit.name}"
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
                    value "${dataElement?.ext.sort().collect { key, value -> "$key: $value"}.join('\n')}"
                    style standardCellStyle
                }
            }
        }
    }




    String printEnumeratedType(DataType dataType){

        if(dataType.instanceOf(EnumeratedType)){
            EnumeratedType enumerated = (EnumeratedType) dataType
            return enumerated.prettyPrint()
        }

        return blank

    }

    String getMultiplicity(Relationship dataElementRelationship){
        if (dataElementRelationship) {
            Map<String, String> ext = dataElementRelationship.ext
            String min = ext.get("Min Occurs") ?: '0'
            String max = ext.get("Max Occurs") ?: '*'
            return "$min..$max"
        }
        else return blank
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
