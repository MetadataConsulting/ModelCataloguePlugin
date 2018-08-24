package org.modelcatalogue.core.dataexport.excel.norththamesreport

import builders.dsl.spreadsheet.builder.api.SheetDefinition
import builders.dsl.spreadsheet.builder.api.SpreadsheetBuilder
import builders.dsl.spreadsheet.builder.poi.PoiSpreadsheetBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.hibernate.SessionFactory
import org.modelcatalogue.core.*
import org.modelcatalogue.core.dataexport.excel.gmcgridreport.GMCGridReportHeaders
import org.modelcatalogue.core.export.inventory.ModelCatalogueStyles
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.gel.export.GridReportXlsxExporter

import builders.dsl.spreadsheet.builder.api.RowDefinition
import builders.dsl.spreadsheet.query.api.SpreadsheetCriteria
import builders.dsl.spreadsheet.query.poi.PoiSpreadsheetCriteria
import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.H1

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
class NorthThamesMappingReportXlsxExporter {

    /**
     * Map of data source systems
     */
    final DataModel sourceModel
    final DataClassService dataClassService
    final DataElementService dataElementService
    final GrailsApplication grailsApplication
    final List<DataModel> mappingModels
    final Boolean mySQL

    List<CatalogueElement> mappedSourceElements = []
    List<CatalogueElement> unmappedSourceElements = []


    protected List<String> excelHeaders = ['LOCAL SITE', 'LOCAL CODESET 1', 'LOCAL CODE 1', 'LOCAL CODE 1 NAME', "LOCAL CODESET 2",	"LOCAL CODE 2",	"LOCAL CODE 2 DESCRIPTION",	"LOINC CODE",	"LOINC CODE DESCRIPTION",	"LOINC SYSTEM(SPECIMEN)",	"GEL CODE",	"GEL CODE DESCRIPTION",	"OPENEHR QUERY", "REF RANGE"]



    /**
     * The report is triggered from a DataModel (element), and is on the
     * location of data elements specified by that DataModel in the given 'organization'.
     * @param element
     * @param dataClassService
     * @param grailsApplication
     * @return
     */
    static NorthThamesMappingReportXlsxExporter create(DataModel sourceModel, DataClassService dataClassService, DataElementService dataElementService, GrailsApplication grailsApplication, Boolean mySQL) {
        return new NorthThamesMappingReportXlsxExporter(sourceModel, dataClassService, dataElementService, grailsApplication, mySQL)
    }


    NorthThamesMappingReportXlsxExporter(DataModel sourceModel, DataClassService dataClassService, DataElementService dataElementService, GrailsApplication grailsApplication, Boolean mySQL) {
        this.sourceModel = sourceModel
        this.dataClassService = dataClassService
        this.grailsApplication = grailsApplication
        this.mappingModels = mappingModels
        this.dataElementService = dataElementService
        this.mySQL = mySQL
    }


    // export will do as it normally does, with new headers, and a different printDataElement, and then do sheetsAfterMainSheetExport at the end.

    static String noSourceMessage = 'No source identified'
    static String multipleSourcesMessage = 'Multiple sources identified, please see entries in the online catalogue'
    static String oneSourceNoMetadataMessage = 'Source identified, metadata not recorded'


    void export(OutputStream outputStream) {
        SpreadsheetBuilder builder = PoiSpreadsheetBuilder.create(outputStream)
        getMappedDataElements()

        builder.build {
            apply ModelCatalogueStyles
            sheet("Mapped Elements") { SheetDefinition sheetDefinition ->
                row {
                    excelHeaders.each { header ->
                        cell {
                            value header
                            width auto
                            style H1
                        }
                    }
                }

                mappedSourceElements.each { DataElement de ->
                    printMapping(de, sheetDefinition)
                }
            }
        }
    }





    void getMappedDataElements(){
        //get all the data elements from the model

        if(mySQL) {
            //populate the mapped elements list with the ones that have relationships
            // and the unmapped element list for the ones that don't have any
            Integer i = 0
            dataElementService.findAllDataElementsInModel([:], sourceModel).getItems().each{ de ->
                i = i+1
                println(i)
                if(de.relatedTo.size()>0){
                    println("mapping" + de.name)
                    mappedSourceElements.add(de)
                }else{
//                    unmappedSourceElements.add(de)
                }
            }
        }else{
            //populate the mapped elements list with the ones that have relationships
            // and the unmapped element list for the ones that don't have any
            print("get mapped elements")
            Integer i = 0
            def dataElements = DataElement.findAllByDataModel(sourceModel)
            dataElements.each{ de ->
                i = i+1
                println(i)
                if(de.relatedTo.size()>0){
                    println("mapping" + de.name)
                    mappedSourceElements.add(de)
                }else{
//                    unmappedSourceElements.add(de)
                }
            }
        }


    }

    void printMapping(DataElement sourceDE, SheetDefinition sheet){

        //print a row with all the mappings form the source models to the mapped models

        //get the mapped items from the source dataelement
        List<CatalogueElement> mappedElements = sourceDE.relatedTo

        //def local1
        //localCodeset1 is the source model
        CatalogueElement local2
        CatalogueElement loinc
        CatalogueElement gel
        CatalogueElement openEHR

        mappedElements.each { CatalogueElement ce ->
            print("printin-" + ce.name)

            String modelName = ce.dataModel.name

            //need to make this more generic
            switch (modelName) {
//                case "localCodeset1":
//                    local1 = ce
//                    break
                case "WinPath":
                    local2 = ce
                    break
                case "LOINC":
                    loinc = ce
                    break
                case "Rare Diseases":
                    gel = ce
                    break
            }
        }



            sheet.with { SheetDefinition sheetDefinition ->
                row {
                    cell {
                        value "RFH"
                        width auto
                        style {
                            wrap text
                            border left, {
                                color black
                                style medium
                            }
                        }
                    }
                    cell {
                        value  "${(sourceDE)?sourceDE.dataModel.name:''}"
                        width auto
                        style {
                            wrap text
                            border left, {
                                color black
                                style medium
                            }
                        }
                    }
                    cell {
                        value "${(sourceDE)?sourceDE.ext.get("Index"):''}"
                        width auto
                        style {
                            wrap text
                            border left, {
                                color black
                                style medium
                            }
                        }
                    }
                    cell {
                        value sourceDE?.name
                        width auto
                        style {
                            wrap text
                            border left, {
                                color black
                                style medium
                            }
                        }
                    }

                    //mapped items

                    cell {
                        value  "${(local2)?local2.dataModel.name:''}"
                        width auto
                        style {
                            wrap text
                            border left, {
                                color black
                                style medium
                            }
                        }
                    }

                    cell {
                        value  "${(local2)?local2.ext.get("WinPath TFC"):''}"
                        width auto
                        style {
                            wrap text
                            border left, {
                                color black
                                style medium
                            }
                        }
                    }
                    cell {
                        value local2?.name
                        width auto
                        style {
                            wrap text
                            border left, {
                                color black
                                style medium
                            }
                        }
                    }

                    //mapped items loinc

                    cell {
                        value  "${(loinc)? (loinc.modelCatalogueId)?: (loinc.latestVersionId)?:loinc.id : ''}"
                        width auto
                        style {
                            wrap text
                            border left, {
                                color black
                                style medium
                            }
                        }
                    }

                    cell {
                        value "${(loinc)?loinc.ext.get("SYSTEM"):''}"
                        width auto
                        style {
                            wrap text
                            border left, {
                                color black
                                style medium
                            }
                        }
                    }
                    cell {
                        value loinc?.name
                        width auto
                        style {
                            wrap text
                            border left, {
                                color black
                                style medium
                            }
                        }
                    }

                    //gel items

                    cell {
                        value "${(gel)? (gel.modelCatalogueId)?: (gel.latestVersionId)?:gel.id : ''}"
                        width auto
                        style {
                            wrap text
                            border left, {
                                color black
                                style medium
                            }
                        }
                    }
                    cell {
                        value gel?.name
                        width auto
                        style {
                            wrap text
                            border left, {
                                color black
                                style medium
                            }
                        }
                    }


                    cell {
                        value   "${(gel)?getOpenEHR(gel):''}"
                        width auto
                        style {
                            wrap text
                            border left, {
                                color black
                                style medium
                            }
                        }
                    }

                    cell {
                        value   "${(sourceDE) ? sourceDE.ext.get("Ref Range"):''}"
                        width auto
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

    String getOpenEHR(CatalogueElement ce){

        List<CatalogueElement> mappedElements = ce.relatedTo
        String openQuery = ''

        mappedElements.each { CatalogueElement me ->
            if(me.dataModel.name=="Open EHR"){
                openQuery =  me.ext.get("Archetype Path Query Statement")
            }
        }
        return openQuery

    }

}
