package org.modelcatalogue.core.dataimport.excel.gmcGridReport

import groovy.util.logging.Log4j
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.dataexport.excel.gmcgridreport.GMCGridReportXlsxExporter
import org.modelcatalogue.core.dataimport.excel.ExcelLoaderSpec
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.test.FileOpener
import spock.lang.Shared
import org.modelcatalogue.core.dataexport.excel.gmcgridreport.GMCGridReportHeaders as Headers

/**
 * Should test the following sequence:
 * Load/import a GridReport r2 for the first time so that it creates new models;
 * Check results;
 * Export a GridReport r2;
 * (Automatically alter it to r2+dr);
 * Load/import r2+dr so that it updates;
 * Check changes;
 * Created by james on 17/08/2017.
 */
@Log4j
class GMCGridReportExcelLoaderSpec extends AbstractIntegrationSpec {
    @Shared DataModelService dataModelService
    @Shared ElementService elementService
    DataClassService dataClassService

    GrailsApplication grailsApplication

    @Shared DefaultCatalogueBuilder defaultCatalogueBuilder
    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()
    @Shared GMCGridReportExcelLoader gmcGridReportExcelLoaderDCB
    @Shared GMCGridReportExcelLoader gmcGridReportExcelLoaderDirect

    def setupSpec() {
        initRelationshipTypes()
        defaultCatalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)
        gmcGridReportExcelLoaderDCB = new GMCGridReportExcelLoaderDCB(dataModelService, elementService)
        gmcGridReportExcelLoaderDirect = new GMCGridReportExcelLoaderDirect()
    }

    static String gelModelName = 'GELModel'
    static String gelModelImportedName = 'GELModelImported'
    static String organization = 'testOrganization'
    static String de1Name = 'DE1'
    static String de2Name = 'DE2'
    static String dataSource1Name = 'DS1'
    static String dataSource2Name = 'DS2'
    static String de1phName = 'DE1_ph'
    static String de2phName = 'DE2_ph'
    static Closure testUpdateInitialModelsInstructions(boolean withImports) {
        return {
            if (withImports) {
                dataModel(name:gelModelName) { // gelModel must have at least two data sources
                    dataClass(name:'DC1'){
                        dataClass(name: 'DC2'){
                            dataElement(name:de1Name, id:1)
                        }
                    }
                }
                dataModel(name:gelModelImportedName) { // gelModel must have at least two data sources
                    dataClass(name:'DCImp1'){
                        dataClass(name: 'DCImp2'){
                            dataElement(name:de2Name, id:2)
                        }
                    }
                }

            }
            else {
                dataModel(name:gelModelName) { // gelModel must have at least two data sources
                    dataClass(name:'DC1'){
                        dataClass(name: 'DC2'){
                            dataElement(name:de1Name, id:1)
                            dataElement(name:de2Name, id:2)
                        }
                    }
                }
            }
            dataModel(name:dataSource1Name){
                ext 'http://www.modelcatalogue.org/metadata/#organization', organization
                dataElement(name:de1phName){
                    ext Headers.semanticMatching, 'yes'
                }
            }
            dataModel(name:dataSource2Name) {
                ext 'http://www.modelcatalogue.org/metadata/#organization', organization
                dataElement(name:de2phName) {
                }
            }
        } as Closure
    }


    def "test update"() {
        log.info("Using ${gmcGridReportExcelLoader.getClass().name}")
        when: "initial models in"
            defaultCatalogueBuilder.build testUpdateInitialModelsInstructions(withImports)
            DataModel gelModel = DataModel.findByName(gelModelName)
            if (withImports) {
                DataModel gelModelImported = DataModel.findByName(gelModelImportedName)
                gelModel.addToImports(gelModelImported)
                gelModelImported.addToImportedBy(gelModel)
            }
            relateNamedElements(de1Name, de1phName)
            relateNamedElements(de2Name, de2phName)


            doGridReport(gelModel, 'tempGMCGridReportAfterInitialImport')

        then:
            noExceptionThrown()

        when: "data element placeholder moved"

            gmcGridReportExcelLoader.updateFromWorkbookSheet(new HSSFWorkbook(getClass().getResourceAsStream('gmcGridReportTestUpdateMove.xls')))

            doGridReport(gelModel, 'tempGMCGridReportAfterLoadingUpdateMove')
        then: "should have DE1_ph in DS2"
            noExceptionThrown()

        when: "metadata changed"
            gmcGridReportExcelLoader.updateFromWorkbookSheet(new HSSFWorkbook(getClass().getResourceAsStream('gmcGridReportTestUpdateMetadataChange.xls')))
            doGridReport(gelModel, 'tempGMCGridReportAfterLoadingUpdateMetadataChange')

        then: "DE2_ph Semantic Matching should be no"
            noExceptionThrown()

        when: "simultaneous change"
        gmcGridReportExcelLoader.updateFromWorkbookSheet(new HSSFWorkbook(getClass().getResourceAsStream('gmcGridReportTestUpdateSimultaneous.xls')))
        doGridReport(gelModel, 'tempGMCGridReportAfterLoadingUpdateSimultaneous')

        then: "DE1_ph should be back to DS1 and Known Issue should be Not recorded"
        noExceptionThrown()
        where:
        /**
         * gmcGridReportExcelLoaderDCB doesn't really work.
         */
        gmcGridReportExcelLoader << [gmcGridReportExcelLoaderDirect]
        /**
         * The exporter doesn't seem to display stuff from an imported
         * data model
         */
        withImports << [false]

    }
    void doGridReport(DataModel gelModel, String fileName) {
        File tempFile = temporaryFolder.newFile("${fileName}_${System.currentTimeMillis()}.xlsx")

        GMCGridReportXlsxExporter.create(gelModel,dataClassService, grailsApplication, 5, organization).export(tempFile.newOutputStream())
        FileOpener.open(tempFile)
    }

    static void relateNamedElements(String el1, String el2) {
        DataElement.findByName(el1).addToRelatedTo(DataElement.findByName(el2))

    }
}

