package org.modelcatalogue.nt.export

import com.google.common.collect.ImmutableMap
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.*
import org.modelcatalogue.core.export.inventory.ModelCatalogueStyles
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.gel.export.GridReportXlsxExporter
import org.modelcatalogue.spreadsheet.builder.api.RowDefinition
import org.modelcatalogue.spreadsheet.builder.api.SheetDefinition
import org.modelcatalogue.spreadsheet.builder.api.SpreadsheetBuilder
import org.modelcatalogue.spreadsheet.builder.poi.PoiSpreadsheetBuilder

import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.H1

/**
 * GridReportXlsxExporter.groovy
 * Purpose: Generate an excel report from a data model, including metadata using the required format
 *
 * @author Adam Milward
 * @version 31/03/2017
 */
class NTGridReportXlsxExporter extends GridReportXlsxExporter {

    Map systemsMap = [:]
    Map metadataCompletion = [:]

    static NTGridReportXlsxExporter create(DataModel element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3) {
        return new NTGridReportXlsxExporter(element, dataClassService, grailsApplication, depth)
    }


    NTGridReportXlsxExporter(CatalogueElement element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3) {
        super(element, dataClassService, grailsApplication, depth)

    }

    private Closure analysisStyle = {
        align center center
        font {
            make bold
            size 16
            color black
        }
    }

    private List<String> ntElementMetadataHeaders = ['Semantic Matching', 'Known Issue', 'Immediate Solution', 'Immediate Solution Owner', 'Long Term Solution', 'Long Term Solution Owner', 'Data Item Unique Code', 'Related To Metadata', 'Part Of Standard Data Set', 'Data Completeness', 'Estimated Quality', 'Timely', 'Comments']
    private List<String> ntElementMetadataKeys = ntElementMetadataHeaders.collect{
        it.replace(/Related To Metadata/, 'Related To')}
    protected List<String> excelHeaders = ['ID', 'Data Element', 'Multiplicity', 'Data Type', 'Validation Rule', 'Business Rule', 'Related To', 'Source System'] + ntElementMetadataHeaders


    @Override
    Map<String, Closure> sheetsAfterMainSheetExport() {
        Map<String, Closure> sheets =
            ['Analysis': {SheetDefinition sheet -> buildAnalysis(sheet)} as Closure] +
            systemsMap.collectEntries {name, v ->
                [(name), {SheetDefinition sheet -> buildCompletionTab(sheet, name)} as Closure]
            }
        println "Sheets: ${sheets}"
        return sheets
    }
    // export will do as it normally does, with new headers, and a different printDataElement, and then do sheetsAfterMainSheetExport at the end.

    @Override
    void printDataElement(RowDefinition rowDefinition, Relationship dataElementRelationship, List outline = []) {
        DataElement dataElement = dataElementRelationship.destination
        List relatedTo  = []
        relatedTo = dataElement.relatedTo.findAll{ it.dataModel.ext.get('http://www.modelcatalogue.org/metadata/#organization') == "UCL" }
        addToSystemsMap(relatedTo, dataElement)

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
                value "${(dataElement.modelCatalogueId)?:(dataElement.getLatestVersionId()) ?: dataElement.getId()}.${dataElement.getVersionNumber()}"
                style {
                    wrap text
                    border top, left, {
                        color black
                        style medium
                    }
                }
            }

            cell() {
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
              "${(dataElement?.dataType?.rule) ?: ""}",
              "${(dataElement?.involvedIn) ? printBusRule(dataElement?.involvedIn) : ""}"].each{
                cellValue ->
                    cell {
                        value cellValue
                        style standardCellStyle
                    }
            }
            cell {
                value "${(relatedTo) ? ((relatedTo.size()==1) ? relatedTo[0]?.name : "Multiple sources identified, please see catalogue") : "No source identified"}"
                if (relatedTo && relatedTo.size()==1) link to url "${ getLoadURL(relatedTo[0]) }"
                style standardCellStyle
            }


            cell {
                value "${getRelatedToModel(relatedTo)}"
                style standardCellStyle
            }

            ntElementMetadataKeys.each{metadataKey ->
                cell {
                    value "${printSystemMetadata(relatedTo, metadataKey)}"
                    style standardCellStyle
                }
            }
        }
    }


    /**
     * Renders analysis
     * @param sheet the current sheet
     * @param systems - the systems that are referenced in the report
     */


    private void buildCompletionTab(SheetDefinition sheet, String system) {


        sheet.with { SheetDefinition sheetDefinition ->

            row{
                excelHeaders.minus(['Multiplicity', 'Related To', 'Source System']).each {
                    cellValue ->
                        cell {
                            value cellValue
                            width auto
                            style H1
                        }
                }
            }

            systemsMap.get(system).each{ de ->
                row() { RowDefinition rowDefinition ->
                    printSystemDataElement(rowDefinition, de)
                }
            }


        }

    }

    private void printSystemDataElement(RowDefinition rowDefinition, DataElement dataElement){
        rowDefinition.with {

            cell() {
                value "${(dataElement.modelCatalogueId)?:(dataElement.getLatestVersionId()) ?: dataElement.getId()}.${dataElement.getVersionNumber()}"
                style {
                    wrap text
                    border top, left, {
                        color black
                        style medium
                    }
                }
            }

            cell() {
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

            ["${(dataElement?.dataType) ? printDataType(dataElement?.dataType) : ""}",
             "${(dataElement?.dataType?.rule) ?: ""}",
             "${(dataElement?.involvedIn) ? printBusRule(dataElement?.involvedIn) : ""}"].each{
                cellValue ->
                    cell {
                        value cellValue
                        style standardCellStyle
                    }
            }
            ntElementMetadataKeys.each{metadataKey ->
                cell {
                    value "${printSystemMetadata([dataElement], metadataKey)}"
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
    }




    /**
     * Renders analysis
     * @param sheet the current sheet
     * @param systems - the systems that are referenced in the report
     */



    private void buildAnalysis(SheetDefinition sheet){
        sheet.with { SheetDefinition sheetDefinition ->


            //source system breakdown
            row {
                cell {
                    value "Source Identified"
                    colspan 5
                    style H1
                }
            }

            row {
                cell {
                    value "System"
                    colspan 4
                    style analysisStyle
                }
                cell {
                    value "Count"
                    width auto
                    style analysisStyle
                }
            }

            systemsMap.each{ k, v ->

                row {
                    cell {
                        value "$k"
                        colspan 4
                    }
                    cell {
                        value "${v.size()}"
                        width auto
                    }
                }
            }

            //metadata completion breakdown

//source system breakdown

            row {
                cell {
                    value " "
                    colspan 4
                }
            }

            row {
                cell {
                    value "Metadata Completion"
                    colspan 5
                    style H1
                }
            }

            row {
                cell {
                    value "Metadata"
                    colspan 4
                    style analysisStyle
                }
                cell {
                    value "Completion"
                    width auto
                    style analysisStyle
                }
            }

            metadataCompletion.each{ k, v ->

                row {
                    cell {
                        value "$k"
                        colspan 4
                    }
                    cell {
                        value "${ (v.get("completed") && v.get("total")) ? Math.round(v.get("completed") / v.get("total") * 100) : "0"} %"
                        width auto
                    }
                }



            }

        }
    }



    String printSystemMetadata(List relatedTo, String metadata){

        Map score = metadataCompletion.get(metadata) ?: [:]
        Integer completed = score.get("completed")?:0
        Integer total = score.get("total")?:0

        if(relatedTo.size()==1){
            if(relatedTo[0].ext.get("$metadata")){
                score.put("completed", completed + 1)
                score.put("total", total + 1)
                metadataCompletion.put(metadata, score)
                return relatedTo[0].ext.get("$metadata")
            }else{
                score.put("total", total + 1)
                metadataCompletion.put(metadata, score)
                return 'Source identified, metadata not recorded'
            }
        }else if(relatedTo.size()>1){

            relatedTo.each{ rel ->
                if(rel.ext.get("$metadata")){
                    score.put("completed", completed + 1)
                    score.put("total", total + 1)
                }else{
                    score.put("completed", completed + 1)
                }
            }
            return "Multiple sources identified, please see entries in the online catalogue"
        }else{
            return "No source identified"
        }



    }

    void addToSystemsMap(List relatedTo, DataElement sourceElement){

        if(relatedTo.size()>0){
            relatedTo.each{ de ->
                String dmname = de.dataModel.name
                List elements =  (systemsMap.get(dmname))?:[]
                elements.add(de)
                systemsMap.put(dmname, elements)
            }
        }else{
            List elements =  (systemsMap.get("No source identified"))?:[]
            elements.add(sourceElement)
            systemsMap.put("No source identified", elements)
        }

    }

    String getRelatedToModel(List relatedTo){
        if(relatedTo.size()==1){
            "${relatedTo[0].dataModel.name}"
        }else if(relatedTo.size()>1){
            "Multiple sources identified, please see entries in the online catalogue"
        }else{
            "No source identified"
        }
    }




}
