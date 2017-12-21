package org.modelcatalogue.core.dataimport.excel

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.audit.AuditService
import spock.lang.Shared

class ConfigStatelessExcelLoaderSpec extends AbstractIntegrationSpec {
//    String dataModelName = 'LOINC_TEST7'
    String loincDataModelName = 'LOINC'
    String goshDataModelName = 'GOSH'
    String lpdcDataModelName = 'WinPath'
    String lpdcRareDataModelName = 'RareDisease'
    String loincHeadersMapXml = 'loinc_headers_map.xml'
    String goshHeadersMapXml = 'gosh_headers_map.xml'
    String lpdcHeadersMapXml = 'lpdc_headers_map.xml'
    String RareDiseaseldpcXml = 'RareDiseaseldpc.xml'
    String lpdcDataFile = 'TFC-TLC_LPDC_With Dups.xlsx'
    String RareDiseaseldpc = 'RareDiseaseldpc.xlsx'
    String testDataModelName = 'TEST_MODEL'
    String testLoincData = 'loinc.xlsx'
    String testGoshData = 'GOSH_lab_test_codes100.xlsx'
    String[] loincDataFiles = ['1loinc.xlsx', '2loinc.xlsx', '3loinc.xlsx', '4loinc.xlsx', '5loinc.xlsx', '6loinc.xlsx', '7loinc.xlsx', '8loinc.xlsx']
    String[] goshDataFiles = ['GOSH lab test codes 1.xlsx', 'GOSH lab test codes 2.xlsx', 'GOSH lab test codes 3.xlsx', 'GOSH lab test codes 4.xlsx', 'GOSH lab test codes 5.xlsx']
    @Shared String lpdcResourcePath = (new File('test/integration/resources/org/modelcatalogue/integration/excel/')).getAbsolutePath()
    @Shared String loincResourcePath = (new File('test/integration/resources/org/modelcatalogue/integration/excel/loinc/')).getAbsolutePath()
    @Shared String goshResourcePath = (new File('test/integration/resources/org/modelcatalogue/integration/excel/goshTestCodes/')).getAbsolutePath()
    ConfigStatelessExcelLoader excelLoader
    def dataModelService, elementService
    AuditService auditService
    DataClassService dataClassService
    GrailsApplication grailsApplication
    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    def loincData() {
        for (String loincDataXlsx in loincDataFiles) {
            excelLoader = new ConfigStatelessExcelLoader(loincDataModelName, new FileInputStream(loincResourcePath + '/' + loincHeadersMapXml))
            excelLoader.buildModelFromStandardWorkbookSheet(null, WorkbookFactory.create(new FileInputStream(loincResourcePath + '/' + loincDataXlsx)))
            excelLoader = null
        }
    }
    def goshData() {
        for (String goshDataXlsx in goshDataFiles) {
            excelLoader = new ConfigStatelessExcelLoader(goshDataModelName, new FileInputStream(goshResourcePath + '/' + goshHeadersMapXml))
            excelLoader.buildModelFromStandardWorkbookSheet(null, WorkbookFactory.create(new FileInputStream(goshResourcePath + '/' + goshDataXlsx)))
            excelLoader = null
        }
    }
    def lpdcData() {
        excelLoader = new ConfigStatelessExcelLoader(lpdcDataModelName, new FileInputStream(lpdcResourcePath + '/' + lpdcHeadersMapXml))
        excelLoader.buildModelFromStandardWorkbookSheet(null, WorkbookFactory.create(new FileInputStream(lpdcResourcePath + '/' + lpdcDataFile)))
        excelLoader = null
    }
    def rareDisease() {
        excelLoader = new ConfigStatelessExcelLoader(lpdcRareDataModelName, new FileInputStream(lpdcResourcePath + '/' + RareDiseaseldpcXml))
        excelLoader.buildModelFromStandardWorkbookSheet(null, WorkbookFactory.create(new FileInputStream(lpdcResourcePath + '/' + RareDiseaseldpc)))
        excelLoader = null
    }
    def testData() {
        excelLoader = new ConfigStatelessExcelLoader(testDataModelName, new FileInputStream(loincResourcePath + '/' + loincHeadersMapXml))
        excelLoader.buildModelFromStandardWorkbookSheet(null, WorkbookFactory.create(new FileInputStream(loincResourcePath + '/' + testLoincData)))
    }
    def setup() {
//        excelLoader = new ConfigStatelessExcelLoader(dataModelName, new FileInputStream(resourcePath + '/' + headersMapXml))
    }
    def "test default catalogue builder imports generic nt dataset"(){

        when: "I load the Excel file"
//        testData()
        auditService.betterMute {
//            loincData()
//            goshData()
//            lpdcData()
//            rareDisease()
            testData()
        }
        then: "new model is created"

//        DataModel.findByName(lpdcDataModelName)
//        DataModel.findByName(loincDataModelName)
//        DataModel.findByName(goshDataModelName)
//        DataModel.findByName(lpdcRareDataModelName)
        DataModel.findByName(testDataModelName)
    }
}
