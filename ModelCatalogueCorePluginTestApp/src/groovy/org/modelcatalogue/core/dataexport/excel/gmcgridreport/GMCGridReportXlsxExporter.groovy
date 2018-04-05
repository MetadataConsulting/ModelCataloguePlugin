package org.modelcatalogue.core.dataexport.excel.gmcgridreport

import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship

import static org.modelcatalogue.gel.export.GridReportXlsxStyles.*
import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.H1
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.gel.export.GridReportXlsxExporter

import builders.dsl.spreadsheet.builder.api.RowDefinition
import builders.dsl.spreadsheet.builder.api.SheetDefinition

import org.modelcatalogue.core.util.builder.BuildProgressMonitor

/**
 * GridReportXlsxExporter.groovy
 * Generates a report from a source data model which, as in GridReportXlsxExporter, shows its data classes in a hierarchy and
 * lists its data elements; furthermore, some of the data elements are linked to a representative placeholder in GMC data models
 * which represent data sources of particular hospitals in a GMC; and the placeholders have associated metadata;
 * the placeholders and their metadata are listed with the data elements.
 *
 * Excel headers format v0.2. At the moment based on UCL data
 * @author Adam Milward
 * @version 31/03/2017
 */
class GMCGridReportXlsxExporter extends GridReportXlsxExporter {

    /**
     * Map of data source systems
     */
    Map systemsMap = [:]
    Map metadataCompletion = [:]
    String organization = ''
    static String defaultOrganization = 'UCLH'
    static String organizationMetadataKey = 'http://www.modelcatalogue.org/metadata/#organization'
    static String dataSourceNameMetadataKey = "http://www.modelcatalogue.org/metadata/#dataSourceName"
    protected List<String> excelHeaders = GMCGridReportHeaders.excelHeaders

    BuildProgressMonitor buildProgressMonitor = null // originally for CatalogueBuilder but using it for monitoring the progress of the export.

    /**
     * The report is triggered from a DataModel (element), and is on the
     * location of data elements specified by that DataModel in the given 'organization'.
     * @param element
     * @param
     * @param grailsApplication
     * @param depth
     * @param organization
     * @return
     */
    static GMCGridReportXlsxExporter create(DataModel element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3, String organization = defaultOrganization) {
        return new GMCGridReportXlsxExporter(element, dataClassService, grailsApplication, depth, organization)
    }

    static GMCGridReportXlsxExporter createWithBuildProgressMonitor(DataModel element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth, String organization, BuildProgressMonitor buildProgressMonitor) {
        GMCGridReportXlsxExporter gmcGridReportXlsxExporter = create(element, dataClassService, grailsApplication, depth, organization)
        gmcGridReportXlsxExporter.buildProgressMonitor = buildProgressMonitor
        return gmcGridReportXlsxExporter
    }

    void logToMonitorIfExists(String message) {
        if (buildProgressMonitor) {
            buildProgressMonitor.onNext(message)
        }
    }


    GMCGridReportXlsxExporter(CatalogueElement element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3, String organization = defaultOrganization) {
        super(element, dataClassService, grailsApplication, depth)
        this.organization = organization

    }


    static String noSourceMessage = 'No source identified'
    static String multipleSourcesMessage = 'Multiple sources identified, please see entries in the online catalogue'
    static String oneSourceNoMetadataMessage = 'Source identified, metadata not recorded'

    @Override
    void printDataElement(RowDefinition rowDefinition, Relationship dataElementRelationship, List outline = []) {

        DataElement dataElement = dataElementRelationship.destination

        logToMonitorIfExists("Printing data element ${dataElement.name}")
        debugLine("GMCGridReportXlsxExporter.printDataElement() dataElement: ${dataElement.name}")

        List placeholders  = []
        placeholders = dataElement.relatedTo.findAll{ it.dataModel.ext.get(organizationMetadataKey) == organization }
        addToSystemsMap(placeholders, dataElement)

        rowDefinition.with {
            outline.each { // Class Hierarchy
                cell(it){
                    style LEFT_BORDER
                }
            }

            cell(depth + 1) { //ID
                value "${(dataElement.modelCatalogueId)?:(dataElement.getLatestVersionId()) ?: dataElement.getId()}.${dataElement.getVersionNumber()}"
                style TOP_LEFT_BORDER
            }

            cell() { // Data Element
                value dataElement.name
                link to url "${getLoadURL(dataElement)}"
                style TOP_LEFT_BORDER
            }
            [ "${getMultiplicity(dataElementRelationship)}",
              "${(dataElement?.dataType) ? printDataType(dataElement?.dataType) : ""}",
              "${(dataElement?.dataType?.rule) ?: ""}",
              "${(dataElement?.involvedIn) ? printBusRule(dataElement?.involvedIn) : ""}"].each{
                cellValue ->
                    cell {
                        value cellValue
                        style STANDARD
                    }
            }
            cell { // Related To
                value "${(placeholders) ? ((placeholders.size()==1) ? placeholders[0]?.name : multipleSourcesMessage) : noSourceMessage}"
                if (placeholders && placeholders.size()==1) link to url "${ getLoadURL(placeholders[0]) }"
                style STANDARD
            }

            Closure sourceSystem = {
                value "${getRelatedToDataSourceName(placeholders)}"
                style STANDARD
            } as Closure

            cell sourceSystem // Source System

            cell sourceSystem // Previously In Source System -- same as Source System
            // 13 keys
            GMCGridReportHeaders.ntElementMetadataKeys.each { metadataKey ->
                cell {
                    value "${printSystemMetadata(placeholders, metadataKey)}"
                    style STANDARD
                }
            }
        }
    }

    /**
     * Write a sheet detailing a particular data source system.
     * @param sheet the current sheet
     * @param systems - the systems that are referenced in the report
     */
    private void writeSystemSheet(SheetDefinition sheet, String system) {


        sheet.with { SheetDefinition sheetDefinition ->

            row {
                excelHeaders.minus(['Multiplicity', 'Related To', 'Source System']).each { cellValue ->
                    cell {
                        value cellValue
                        width auto
                        style H1
                    }
                }
            }

            systemsMap.get(system).each { de ->
                row() { RowDefinition rowDefinition ->
                    printSystemDataElement(rowDefinition, de)
                }
            }
        }
    }
    /**
     * Helper for #writeSystemSheet
     * @param rowDefinition
     * @param dataElement
     */
    private void printSystemDataElement(RowDefinition rowDefinition, DataElement dataElement){
        rowDefinition.with {

            cell() {
                value "${(dataElement.modelCatalogueId)?:(dataElement.getLatestVersionId()) ?: dataElement.getId()}.${dataElement.getVersionNumber()}"
                style TOP_LEFT_BORDER
            }

            cell() {
                value dataElement.name
                link to url "${getLoadURL(dataElement)}"
                style TOP_LEFT_BORDER
            }

            ["${(dataElement?.dataType) ? printDataType(dataElement?.dataType) : ""}",
             "${(dataElement?.dataType?.rule) ?: ""}",
             "${(dataElement?.involvedIn) ? printBusRule(dataElement?.involvedIn) : ""}"].each { cellValue ->
                cell {
                    value cellValue
                    style STANDARD
                }
            }
            GMCGridReportHeaders.ntElementMetadataKeys.each { metadataKey ->
                cell {
                    value "${printSystemMetadata([dataElement], metadataKey)}"
                    style TOP_BORDER
                }
            }
        }
    }

    /**
     * Renders analysis of what data comes from where, and how much metadata is filled in.
     * @param sheet the current sheet
     * @param systems - the systems that are referenced in the report
     */
    private void writeAnalysis(SheetDefinition sheet){
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
                    style ANALYSIS
                }
                cell {
                    value "Count"
                    width auto
                    style ANALYSIS
                }
            }

            systemsMap.each { k, v ->
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
                    style ANALYSIS
                }
                cell {
                    value "Completion"
                    width auto
                    style ANALYSIS
                }
            }

            metadataCompletion.each { k, v ->
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

    /**
     * Helper for writeAnalysis
     * @param relatedTo
     * @param metadata
     * @return
     */
    private String printSystemMetadata(List relatedTo, String metadata){

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
                return oneSourceNoMetadataMessage
            }
        } else if (relatedTo.size() > 1) {
            relatedTo.each { rel ->
                if (rel.ext.get("$metadata")){
                    score.put("completed", completed + 1)
                    score.put("total", total + 1)
                } else {
                    score.put("completed", completed + 1)
                }
            }
            return multipleSourcesMessage
        } else {
            return noSourceMessage
        }
    }

    private void addToSystemsMap(List relatedTo, DataElement sourceElement){

        if (relatedTo.size() > 0) {
            relatedTo.each { de ->
                String dmname = de.dataModel.name
                List elements = (systemsMap.get(dmname))?:[]
                elements.add(de)
                systemsMap.put(dmname, elements)
            }
        } else {
            List elements =  (systemsMap.get(noSourceMessage))?:[]
            elements.add(sourceElement)
            systemsMap.put(noSourceMessage, elements)
        }
    }

    /**
     * Not the same as the Data Model name! Data Model name has organization and GEL model added on.
     * @param relatedTo
     * @return
     */
    String getRelatedToDataSourceName(List relatedTo){
        if (relatedTo.size() == 1){
            "${relatedTo[0].dataModel.ext.get(dataSourceNameMetadataKey)}"
        } else if (relatedTo.size() > 1){
            multipleSourcesMessage
        } else {
            noSourceMessage
        }
    }
}
