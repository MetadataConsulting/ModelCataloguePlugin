package org.modelcatalogue.core.dataImport.excel

import org.modelcatalogue.core.dataimport.excel.ExcelImportType
import spock.lang.Specification

/**
 * Created by james on 01/12/2017.
 */
class ExcelImportTypeSpec extends Specification {
    def "Test humanReadableNames"() {
        setup:
        String goshLabTestCodesHumanReadableName = "GOSH Lab Test Codes"
        expect:

        assert ExcelImportType.humanReadableNames.toSet().containsAll(["Configurable (XML Map)", goshLabTestCodesHumanReadableName])
        assert ExcelImportType.fromHumanReadableName(goshLabTestCodesHumanReadableName) == ExcelImportType.GOSH_LAB_TEST_CODES
    }
}
