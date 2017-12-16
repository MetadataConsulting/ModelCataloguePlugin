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
    String loincHeadersMapXml = 'loinc_headers_map.xml'
    String goshHeadersMapXml = 'gosh_headers_map.xml'
    String lpdcHeadersMapXml = 'lpdc_headers_map.xml'
    String lpdcDataFile = 'TFC-TLC_LPDC_With Dups.xlsx'
    String[] loincDataFiles = ['1loinc.xlsx', '2loinc.xlsx', '3loinc.xlsx', '4loinc.xlsx', '5loinc.xlsx', '6loinc.xlsx', '7loinc.xlsx', '8loinc.xlsx']
    String[] goshDataFiles = ['GOSH lab test codes 1.xlsx', 'GOSH lab test codes 2.xlsx', 'GOSH lab test codes 3.xlsx', 'GOSH lab test codes 4.xlsx', 'GOSH lab test codes 5.xlsx']
    @Shared String lpdcResourcePath = (new File('test/integration/resources/org/modelcatalogue/integration/excel/')).getAbsolutePath()
    @Shared String loincResourcePath = (new File('test/integration/resources/org/modelcatalogue/integration/excel/loinc/')).getAbsolutePath()
    @Shared String goshResourcePath = (new File('test/integration/resources/org/modelcatalogue/integration/excel/goshTestCodes/')).getAbsolutePath()
    ConfigStatelessExcelLoader excelLoader
//    CatalogueBuilder catalogueBuilder
    def dataModelService, elementService
    AuditService auditService
    DataClassService dataClassService
    GrailsApplication grailsApplication
    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    def setup() {
//        XMLUnit.ignoreWhitespace = true
//        XMLUnit.ignoreComments = true
//        XMLUnit.ignoreAttributeOrder = true
//        catalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)
//        excelLoader = new ConfigStatelessExcelLoader(dataModelName, new FileInputStream(resourcePath + '/' + headersMapXml))
    }

    def "test default catalogue builder imports generic nt dataset"(){

        when: "I load the Excel file"
//        excelLoader.buildModelFromStandardWorkbookSheet(null, WorkbookFactory.create(new FileInputStream(resourcePath + '/' + dataXlsx)))
        auditService.betterMute {
            for (String loincDataXlsx in loincDataFiles) {
                excelLoader = new ConfigStatelessExcelLoader(loincDataModelName, new FileInputStream(loincResourcePath + '/' + loincHeadersMapXml))
                excelLoader.buildModelFromStandardWorkbookSheet(null, WorkbookFactory.create(new FileInputStream(loincResourcePath + '/' + loincDataXlsx)))
                excelLoader = null
            }
            for (String goshDataXlsx in goshDataFiles) {
                excelLoader = new ConfigStatelessExcelLoader(goshDataModelName, new FileInputStream(goshResourcePath + '/' + goshHeadersMapXml))
                excelLoader.buildModelFromStandardWorkbookSheet(null, WorkbookFactory.create(new FileInputStream(goshResourcePath + '/' + goshDataXlsx)))
                excelLoader = null
            }
            excelLoader = new ConfigStatelessExcelLoader(lpdcDataModelName, new FileInputStream(lpdcResourcePath + '/' + lpdcHeadersMapXml))
            excelLoader.buildModelFromStandardWorkbookSheet(null, WorkbookFactory.create(new FileInputStream(lpdcResourcePath + '/' + lpdcDataFile)))
            excelLoader = null
//            excelLoader.buildModelFromStandardWorkbookSheet(null, WorkbookFactory.create(new FileInputStream(resourcePath + '/' + dataXlsx)))
        }
        then: "new model is created"

//        DataModel.findByName(lpdcDataModelName)
//        DataModel.findByName(loincDataModelName)
        DataModel.findByName(goshDataModelName)
    }
}
