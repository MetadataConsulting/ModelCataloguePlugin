package org.modelcatalogue.core.dataimport.excel.gmcGridReport

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.dataimport.excel.ExcelLoaderSpec
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.test.FileOpener
import org.modelcatalogue.core.xml.CatalogueXmlImportSpec
import spock.lang.Shared
import org.modelcatalogue.core.dataimport.excel.gmcGridReport.GMCGridReportHeaders as Headers

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
class GMCGridReportExcelLoaderSpec extends ExcelLoaderSpec {
    @Shared DataModelService dataModelService
    @Shared ElementService elementService
    DataClassService dataClassService

    GrailsApplication grailsApplication

    @Shared DefaultCatalogueBuilder defaultCatalogueBuilder
    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()
    @Shared GMCGridReportExcelLoader gmcGridReportExcelLoader

    def setupSpec() {
        initRelationshipTypes()
        defaultCatalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)
        gmcGridReportExcelLoader = new GMCGridReportExcelLoader(dataModelService, elementService)
    }

    static String gelModelName = 'GELModel'
    static String organization = 'testOrganization'
    static String de1Name = 'DE1'
    static String de2Name = 'DE2'
    static String dataSource1Name = 'DS1'
    static String dataSource2Name = 'DS2'
    static String de1phName = 'DE1_ph'
    static String de2phName = 'DE2_ph'
    static Closure testUpdateInitialModelsInstructions = {
        dataModel(name:gelModelName) { // gelModel must have at least two data sources
            dataClass(name:'DC1'){
                dataClass(name: 'DC2'){
                    dataElement(name:de1Name, id:1)
                    dataElement(name:de2Name, id:2)
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
                ext Headers.semanticMatching, 'yes' // initial data element placeholders MUST have metadata.
            }
        }
    } as Closure


    def "test update"() {
        when: "initial models in"
            gmcGridReportExcelLoader.defaultCatalogueBuilder.build testUpdateInitialModelsInstructions
            relateNamedElements(de1Name, de1phName)
            relateNamedElements(de2Name, de2phName)

            DataModel gelModel = DataModel.findByName(gelModelName)
            doGridReport(gelModel, 'tempGMCGridReportAfterInitialImport')

        then:
            noExceptionThrown()

        when: "data element placeholder moved"

            gmcGridReportExcelLoader.updateFromWorkbook(new HSSFWorkbook(getClass().getResourceAsStream('gmcGridReportTestUpdate1.xls')))

            doGridReport(gelModel, 'tempGMCGridReportAfterLoadingUpdatedSpreadsheet1')
        then:
            noExceptionThrown()

        when: "metadata changed"
            gmcGridReportExcelLoader.updateFromWorkbook(new HSSFWorkbook(getClass().getResourceAsStream('gmcGridReportTestUpdate2.xls')))
            doGridReport(gelModel, 'tempGMCGridReportAfterLoadingUpdatedSpreadsheet2')
        /**
         * OK so this is not really working. TestUpdate2 would change the metadata on DE2 placeholder but it does not relink things properly.
         */
        then:
            noExceptionThrown()

    }
    void doGridReport(DataModel gelModel, String fileName) {
        File tempFile = temporaryFolder.newFile("${fileName}_${System.currentTimeMillis()}.xlsx")

        GMCGridReportXlsxExporter.create(gelModel,dataClassService, grailsApplication, 5, organization).export(tempFile.newOutputStream())
        FileOpener.open(tempFile)
    }

    void relateNamedElements(String el1, String el2) {
        DataElement.findByName(el1).addToRelatedTo(DataElement.findByName(el2))

    }
}

