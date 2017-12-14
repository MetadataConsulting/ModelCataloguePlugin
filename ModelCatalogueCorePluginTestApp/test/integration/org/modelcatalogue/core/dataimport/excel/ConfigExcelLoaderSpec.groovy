package org.modelcatalogue.core.dataimport.excel

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataModel
import spock.lang.Shared
import spock.lang.Unroll

class ConfigExcelLoaderSpec extends AbstractIntegrationSpec {
    @Shared String resourcePath = (new File("test/integration/resources/org/modelcatalogue/integration/excel")).getAbsolutePath()

    @Unroll
    def "importing #dataXlsx with headers #headersMapXml builds model #dataModelName"(String dataModelName, String headersMapXml, String dataXlsx) {
        given:

        ConfigExcelLoader excelLoader = new ConfigExcelLoader(dataModelName, new FileInputStream(resourcePath + '/' + headersMapXml))

        expect:
        !DataModel.findByName(dataModelName)

        when: "I load the Excel file"
        excelLoader.buildModelFromStandardWorkbookSheet(null, WorkbookFactory.create(new FileInputStream(resourcePath + '/' + dataXlsx)))
//            auditService.mute {
//                excelLoader.buildModelFromStandardWorkbookSheet(null, WorkbookFactory.create(new FileInputStream(resourcePath + '/' + dataXlsx)))
//            }
        then: "new model is created"
        DataModel.findByName(dataModelName)

        where:
        dataModelName  | headersMapXml           | dataXlsx
        'LOINC_TEST01' | 'loinc_headers_map.xml' | 'loinc.xlsx'
        'GOSH_TEST1'   | 'gosh_headers_map.xml'  | 'GOSH_lab_test_codes100.xlsx'
    }
}
