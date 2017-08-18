package org.modelcatalogue.core.dataimport.excel.gmcGridReport

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

    def setupSpec() {
        initRelationshipTypes()
        defaultCatalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)
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
        dataModel(name:gelModelName) {
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
            dataElement(name:de2phName)
        }
    } as Closure

    static Closure testUpdateModelsUpdate1Instructions = {
        dataModel(name:dataSource2Name) {
            dataElement(name:de1phName) {
                ext 'EXT1', 'EXTVAL1'
            }
        }
    } as Closure

    static Closure testUpdateModelsUpdate2Instructions = {
        dataModel(name:dataSource1Name){
            dataElement(name:de1phName){
                ext 'EXT1', 'EXTVAL2'
                ext 'EXT2', 'EXTVAL3'
            }
        }
    } as Closure

    def "test update"() {
        when: "initial models in"
            defaultCatalogueBuilder.build testUpdateInitialModelsInstructions
            relateNamedElements(de1Name, de1phName)
            relateNamedElements(de2Name, de2phName)

            DataModel gelModel = DataModel.findByName(gelModelName)

            File tempFile = temporaryFolder.newFile("tempFile${System.currentTimeMillis()}.xlsx")

            GMCGridReportXlsxExporter.create(gelModel,dataClassService, grailsApplication, 5, organization).export(tempFile.newOutputStream())
            FileOpener.open(tempFile)
        then:
            noExceptionThrown()

        when: "data element placeholder moved"
            GMCGridReportExcelLoader

    }
    void relateNamedElements(String el1, String el2) {
        DataElement.findByName(el1).addToRelatedTo(DataElement.findByName(el2))

    }
}

