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
import spock.lang.Ignore
import spock.lang.Unroll

class ConfigStatelessExcelLoaderSpec extends AbstractIntegrationSpec {

    ConfigStatelessExcelLoader excelLoader

    @Unroll
    void "buildModelFromStandardWorkbookSheet works with #dataFile"(String dataFile, String dataModelName, String resourcePath, String headersMapXml) {

        when:
        File f = new File(resourcePath)

        then:
        f.exists()

        when:
        String path = f.absolutePath
        f = new File(path + '/' + headersMapXml)

        then:
        f.exists()

        when:
        f = new File(path + '/' + dataFile)

        then:
        f.exists()

        when:
        excelLoader = new ConfigStatelessExcelLoader(dataModelName, new FileInputStream(path + '/' + headersMapXml))
        excelLoader.buildModelFromStandardWorkbookSheet(null, WorkbookFactory.create(new FileInputStream(path + '/' + dataFile)))
        excelLoader = null

        then:
        DataModel.findByName(dataModelName)

        where:
        dataFile                      | dataModelName      | resourcePath                                                                      | headersMapXml
        'GOSH_lab_test_codes100.xlsx'  | 'GOSH'             | 'test/integration/resources/org/modelcatalogue/integration/excel/goshTestCodes/'  | 'gosh_headers_map.xml'
        //'TFC-TLC_LPDC_With Dups.xlsx' | 'WinPath'          | 'test/integration/resources/org/modelcatalogue/integration/excel/'                | 'lpdc_headers_map.xml'
        //'RareDiseaseldpc.xlsx'        | 'RareDisease'      | 'test/integration/resources/org/modelcatalogue/integration/excel/'                | 'RareDiseaseldpc.xml'
        'loinc.xlsx'                  | 'LOINC'            | 'test/integration/resources/org/modelcatalogue/integration/excel/loinc/'          | 'loinc_headers_map.xml'
    }
}
