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

class ConfigExcelLoaderSpec extends AbstractIntegrationSpec {
    Boolean doGosh = false
//    String dataModelName = 'LOINC_TEST7'
    String dataModelName = doGosh ? 'GOSH_TEST1' : 'LOINC_TEST03'
    String headersMapXml = doGosh ? 'gosh_headers_map.xml' : 'loinc_headers_map.xml'
    String dataXlsx = doGosh ? 'GOSH_lab_test_codes100.xlsx' : 'loinc1000.xlsx' // 'loinc_edit.xlsx'
    @Shared String resourcePath = (new File("test/integration/resources/org/modelcatalogue/integration/excel/${doGosh ? 'goshTestCodes' : 'loinc'}")).getAbsolutePath()
    ConfigExcelLoader excelLoader
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
        excelLoader = new ConfigExcelLoader(dataModelName, new FileInputStream(resourcePath + '/' + headersMapXml))
    }

    def "test default catalogue builder imports generic nt dataset"(){

        when: "I load the Excel file"
//        excelLoader.buildModelFromStandardWorkbookSheet(null, WorkbookFactory.create(new FileInputStream(resourcePath + '/' + dataXlsx)))
            auditService.betterMute {
                excelLoader.buildModelFromStandardWorkbookSheet(null, WorkbookFactory.create(new FileInputStream(resourcePath + '/' + dataXlsx)))
            }
        then: "new model is created"

        DataModel.findByName(dataModelName)
    }
}
