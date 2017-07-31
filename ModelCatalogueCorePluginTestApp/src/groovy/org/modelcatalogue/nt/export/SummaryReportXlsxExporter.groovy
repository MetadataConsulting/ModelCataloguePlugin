package org.modelcatalogue.nt.export

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
 * GridReportXlsxExporter.groovy
 * Purpose: Generate an excel report from a data model, including metadata using the required format
 *
 * @author Adam Milward
 * @version 31/03/2017
 */
class SummaryReportXlsxExporter {

    final CatalogueElement element
    final DataClassService dataClassService
    final GrailsApplication grailsApplication
    final int depth
    Map systemsMap = [:]
    Map metadataCompletion = [:]

    static SummaryReportXlsxExporter create(DataModel element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3) {
        return new SummaryReportXlsxExporter(element, dataClassService, grailsApplication, depth)
    }


    private SummaryReportXlsxExporter(CatalogueElement element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3) {
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
            sheet("$element.name $element.dataModelSemanticVersion") { SheetDefinition sheetDefinition ->
                row {
                    cell {
                        value 'Class Hierarchy'
                        colspan depth
                        style H1
                    }
                    cell {
                        value 'ID'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Data Element'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Multiplicity'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Data Type'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Validation Rule'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Business Rule'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Related To'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Source System'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Semantic Matching'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Known Issue'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Immediate Solution'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Immediate Solution Owner'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Long Term Solution'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Long Term Solution Owner'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Data Item Unique Code'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Related To Metadata'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Part Of Standard Data Set'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Data Completeness'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Estimated Quality'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Timely'
                        width auto
                        style H1
                    }
                    cell {
                        value 'Comments'
                        width auto
                        style H1
                    }
                }

                dataClasses.each { dataClass ->
                    buildRows(sheetDefinition, dataClass.getOutgoingRelationshipsByType(RelationshipType.hierarchyType), 1, 2)
                }

            }



            sheet("Analysis") { SheetDefinition sheet ->

                buildAnalysis(sheet)
            }

            systemsMap.each { k, v ->


                sheet(k) { SheetDefinition sheet ->
                    buildCompletionTab(sheet, k)
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
                cell {
                    value 'ID'
                    width auto
                    style H1
                }

                cell {
                    value 'Data Element'
                    width auto
                    style H1
                }
                cell {
                    value 'Data Type'
                    width auto
                    style H1
                }
                cell {
                    value 'Validation Rule'
                    width auto
                    style H1
                }
                cell {
                    value 'Business Rule'
                    width auto
                    style H1
                }
                cell {
                    value 'Semantic Matching'
                    width auto
                    style H1
                }
                cell {
                    value 'Known Issue'
                    width auto
                    style H1
                }
                cell {
                    value 'Immediate Solution'
                    width auto
                    style H1
                }
                cell {
                    value 'Immediate Solution Owner'
                    width auto
                    style H1
                }
                cell {
                    value 'Long Term Solution'
                    width auto
                    style H1
                }
                cell {
                    value 'Long Term Solution Owner'
                    width auto
                    style H1
                }
                cell {
                    value 'Data Item Unique Code'
                    width auto
                    style H1
                }
                cell {
                    value 'Related To Metadata'
                    width auto
                    style H1
                }
                cell {
                    value 'Part Of Standard Data Set'
                    width auto
                    style H1
                }
                cell {
                    value 'Data Completeness'
                    width auto
                    style H1
                }
                cell {
                    value 'Estimated Quality'
                    width auto
                    style H1
                }
                cell {
                    value 'Timely'
                    width auto
                    style H1
                }
                cell {
                    value 'Comments'
                    width auto
                    style H1
                }
            }

            systemsMap.get(system).each{ de ->
                row() { RowDefinition rowDefinition ->
                    printSystemDataElement(rowDefinition, de)
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
                    style {
                        align center center
                        font {
                            make bold
                            size 16
                            color black
                        }
                    }
                }
                cell {
                    value "Count"
                    width auto
                    style {
                        align center center
                        font {
                            make bold
                            size 16
                            color black
                        }
                    }
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
                    style {
                        align center center
                        font {
                            make bold
                            size 16
                            color black
                        }
                    }
                }
                cell {
                    value "Completion"
                    width auto
                    style {
                        align center center
                        font {
                            make bold
                            size 16
                            color black
                        }
                    }
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

                cell {
                    value "${getMultiplicity(dataElementRelationship)}"
                    style {
                        wrap text
                        border top,  {
                            color black
                            style medium
                        }
                    }
                }

                cell {
                    value "${(dataElement?.dataType) ? printDataType(dataElement?.dataType) : ""}"
                    style {
                        wrap text
                        border top, {
                            color black
                            style medium
                        }
                    }

                }

                cell {
                    value "${(dataElement?.dataType?.rule) ? dataElement?.dataType?.rule : ""}"
                    style {
                        wrap text
                        border top, {
                            color black
                            style medium
                        }
                    }
                }

                cell {
                    value "${(dataElement?.involvedIn) ? printBusRule(dataElement?.involvedIn) : ""}"
                    style {
                        wrap text
                        border top, {
                            color black
                            style medium
                        }
                    }
                }

                cell {
                    value "${getRelatedToModel(relatedTo)}"
                    style {
                        wrap text
                        border top,  {
                            color black
                            style medium
                        }
                    }
                }

                cell {
                    value "${(relatedTo) ? ((relatedTo.size()==1) ? relatedTo[0]?.name : "Multiple sources identified, please see catalogue") : "No source identified"}"
                    if (relatedTo && relatedTo.size()==1) link to url "${ getLoadURL(relatedTo[0]) }"
                    style {
                        wrap text
                        border top, {
                            color black
                            style medium
                        }
                    }
                }


                cell {
                    value "${ printSystemMetadata(relatedTo, 'Semantic Matching')}"
                    style {
                        wrap text
                        border top,  {
                            color black
                            style medium
                        }
                    }
                }

                cell {
                    value "${printSystemMetadata(relatedTo, 'Known Issue')}"
                    style {
                        wrap text
                        border top,  {
                            color black
                            style medium
                        }
                    }
                }

                cell {
                    value "${printSystemMetadata(relatedTo, 'Immediate Solution')}"
                    style {
                        wrap text
                        border top,  {
                            color black
                            style medium
                        }
                    }
                }

                cell {
                    value "${printSystemMetadata(relatedTo, 'Immediate Solution Owner')}"
                    style {
                        wrap text
                        border top,  {
                            color black
                            style medium
                        }
                    }
                }

                cell {
                    value "${printSystemMetadata(relatedTo, 'Long Term Solution')}"
                    style {
                        wrap text
                        border top,  {
                            color black
                            style medium
                        }
                    }
                }

                cell {
                    value "${printSystemMetadata(relatedTo, 'Long Term Solution Owner')}"
                    style {
                        wrap text
                        border top,  {
                            color black
                            style medium
                        }
                    }
                }

                cell {
                    value "${printSystemMetadata(relatedTo, 'Data Item Unique Code')}"
                    style {
                        wrap text
                        border top,  {
                            color black
                            style medium
                        }
                    }
                }

                cell {
                    value "${printSystemMetadata(relatedTo, 'Related To')}"
                    style {
                        wrap text
                        border top,  {
                            color black
                            style medium
                        }
                    }
                }

                cell {
                    value "${printSystemMetadata(relatedTo, 'Part Of Standard Data Set')}"
                    style {
                        wrap text
                        border top,  {
                            color black
                            style medium
                        }
                    }
                }

                cell {
                    value "${printSystemMetadata(relatedTo, 'Data Completeness')}"
                    style {
                        wrap text
                        border top,  {
                            color black
                            style medium
                        }
                    }
                }
                cell {
                    value "${printSystemMetadata(relatedTo, 'Estimated Quality')}"
                    style {
                        wrap text
                        border top,  {
                            color black
                            style medium
                        }
                    }
                }
                cell {
                    value "${printSystemMetadata(relatedTo, 'Timely')}"
                    style {
                        wrap text
                        border top,  {
                            color black
                            style medium
                        }
                    }
                }
                cell {
                    value "${printSystemMetadata(relatedTo, 'Comments')}"
                    style {
                        wrap text
                        border top,  {
                            color black
                            style medium
                        }
                    }
                }

            }
        }



    void printSystemDataElement(RowDefinition rowDefinition, DataElement dataElement){
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
                link to url "${dataElement.defaultModelCatalogueId.split("/catalogue")[0] + "/load?" + dataElement.defaultModelCatalogueId}"
                style {
                    wrap text
                    border top, left, {
                        color black
                        style medium
                    }
                }
            }


            cell {
                value "${(dataElement?.dataType) ? printDataType(dataElement?.dataType) : ""}"
                style {
                    wrap text
                    border top, {
                        color black
                        style medium
                    }
                }

            }

            cell {
                value "${(dataElement?.dataType?.rule) ? dataElement?.dataType?.rule : ""}"
                style {
                    wrap text
                    border top, {
                        color black
                        style medium
                    }
                }
            }

            cell {
                value "${(dataElement?.involvedIn) ? printBusRule(dataElement?.involvedIn) : ""}"
                style {
                    wrap text
                    border top, {
                        color black
                        style medium
                    }
                }
            }


            cell {
                value "${ printSystemMetadata([dataElement], 'Semantic Matching')}"
                style {
                    wrap text
                    border top,  {
                        color black
                        style medium
                    }
                }
            }

            cell {
                value "${printSystemMetadata([dataElement], 'Known Issue')}"
                style {
                    wrap text
                    border top,  {
                        color black
                        style medium
                    }
                }
            }

            cell {
                value "${printSystemMetadata([dataElement], 'Immediate Solution')}"
                style {
                    wrap text
                    border top,  {
                        color black
                        style medium
                    }
                }
            }

            cell {
                value "${printSystemMetadata([dataElement], 'Immediate Solution Owner')}"
                style {
                    wrap text
                    border top,  {
                        color black
                        style medium
                    }
                }
            }

            cell {
                value "${printSystemMetadata([dataElement], 'Long Term Solution')}"
                style {
                    wrap text
                    border top,  {
                        color black
                        style medium
                    }
                }
            }

            cell {
                value "${printSystemMetadata([dataElement], 'Long Term Solution Owner')}"
                style {
                    wrap text
                    border top,  {
                        color black
                        style medium
                    }
                }
            }

            cell {
                value "${printSystemMetadata([dataElement], 'Data Item Unique Code')}"
                style {
                    wrap text
                    border top,  {
                        color black
                        style medium
                    }
                }
            }

            cell {
                value "${printSystemMetadata([dataElement], 'Related To')}"
                style {
                    wrap text
                    border top,  {
                        color black
                        style medium
                    }
                }
            }

            cell {
                value "${printSystemMetadata([dataElement], 'Part Of Standard Data Set')}"
                style {
                    wrap text
                    border top,  {
                        color black
                        style medium
                    }
                }
            }

            cell {
                value "${printSystemMetadata([dataElement], 'Data Completeness')}"
                style {
                    wrap text
                    border top,  {
                        color black
                        style medium
                    }
                }
            }
            cell {
                value "${printSystemMetadata([dataElement], 'Estimated Quality')}"
                style {
                    wrap text
                    border top,  {
                        color black
                        style medium
                    }
                }
            }
            cell {
                value "${printSystemMetadata([dataElement], 'Timely')}"
                style {
                    wrap text
                    border top,  {
                        color black
                        style medium
                    }
                }
            }
            cell {
                value "${printSystemMetadata([dataElement], 'Comments')}"
                style {
                    wrap text
                    border top,  {
                        color black
                        style medium
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
