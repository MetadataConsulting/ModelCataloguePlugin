package org.modelcatalogue.core.dataimport.excel

import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.dataimport.ProcessDataRowsDaoService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

class ConfigStatelessExcelLoaderSpec extends AbstractIntegrationSpec {

    @Autowired
    AuditService auditService

    @Autowired
    ProcessDataRowsDaoService processDataRowsDaoService

    @Unroll
    def "build model #name from #fileName #headersMap"(String name, String headersMap, String path, String fileName) {

        when:
        String resourcePath = (new File('test/integration/resources/org/modelcatalogue/integration/excel/' + path)).getAbsolutePath()

        then:
        processDataRowsDaoService
        resourcePath

        when:
        FileInputStream fileInputStream = new FileInputStream(resourcePath + '/' + fileName)

        then:
        fileInputStream

        when: "I load the Excel file"
        auditService.betterMute {
            ConfigStatelessExcelLoader excelLoader = new ConfigStatelessExcelLoader(name,
                    new FileInputStream(resourcePath + '/' + headersMap),
                    processDataRowsDaoService)
            Workbook wb = WorkbookFactory.create(fileInputStream)
            excelLoader.buildModelFromStandardWorkbookSheet(null, wb)
        }
        then: "new model is created"
        DataModel.findByName(name)

        where:
        name      | headersMap              | path             | fileName
        'LOINC'   | 'loinc_headers_map.xml' | 'loinc/'         | 'loinc.xlsx'
        'GOSH'    | 'gosh_headers_map.xml'  | 'goshTestCodes/' | 'GOSH_lab_test_codes.xlsx'
    }
}
