package org.modelcatalogue.core.dataimport.excel

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.junit.Rule
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.audit.AuditService
import spock.lang.IgnoreIf
import spock.lang.Unroll

@IgnoreIf({
    System.getProperty('IGNORE_OFFICE')
})
class ConfigExcelLoaderSpec extends AbstractIntegrationSpec {
    def dataModelService
    AuditService auditService
    DataClassService dataClassService
    ElementService elementService
    GrailsApplication grailsApplication

    @Unroll
    def "test default catalogue builder imports generic nt dataset #dataModelName"(String path, String dataModelName, String headersMapXml, String dataXlsx) {
        given:
        String resourcePath = (new File("test/integration/resources/org/modelcatalogue/integration/excel/${path}")).getAbsolutePath()
        ConfigExcelLoader excelLoader = new ConfigExcelLoader(dataModelName, new FileInputStream(resourcePath + '/' + headersMapXml), elementService)

        when: "I load the Excel file"
        auditService.betterMute {
            excelLoader.buildModelFromStandardWorkbookSheet(null, WorkbookFactory.create(new FileInputStream(resourcePath + '/' + dataXlsx)))
        }

        then: "new model is created"
        DataModel.findByName(dataModelName)

        where:
        path            | dataModelName  | headersMapXml           | dataXlsx
//        'loinc'         | 'LOINC_TEST03' | 'loinc_headers_map.xml' | 'loinc1000.xlsx'
//        'goshTestCodes' | 'GOSH_TEST1'   | 'gosh_headers_map.xml'  | 'GOSH_lab_test_codes100.xlsx'
        'cosd' | 'COSD'   | 'cosd_map.xml'  | 'cosd.xlsx'
    }
}
