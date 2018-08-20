package org.modelcatalogue.core.dataimport.excel

import grails.transaction.Transactional
import org.modelcatalogue.core.MDXFeaturesEnum
import org.modelcatalogue.core.MDXFeaturesService

@Transactional
class ExcelImportTypeService {

    MDXFeaturesService mdxFeaturesService

    /**
     * ExcelImportTypes filtered by feature
     * @return
     */
    List<ExcelImportType> filteredExcelImportTypes() {
        List<ExcelImportType> excelImportTypes = ExcelImportType.values()
        if (!mdxFeaturesService.getMDXFeatures().northThamesFeatures) {
            excelImportTypes.removeIf {ExcelImportType importType ->
                importType.mdxFeaturesEnum == MDXFeaturesEnum.NORTH_THAMES
            }
        }
        return excelImportTypes
    }
}

/**
 * Created by james on 01/12/2017.
 */
enum ExcelImportType {
    CONFIG('Configurable (XML Map)', MDXFeaturesEnum.VANILLA),
    //    LOINC('LOINC'),
    GOSH_LAB_TEST_CODES('GOSH Lab Test Codes', MDXFeaturesEnum.NORTH_THAMES),
    NORTH_THAMES_DATA_SOURCE_MAPPING('North Thames Data Source Mapping', MDXFeaturesEnum.NORTH_THAMES),
//    REIMPORT_FROM_EXCEL_EXPORTER('Re-import from Excel Exporter'),
//    STANDARD('Standard')

    final String humanReadableName
    final MDXFeaturesEnum mdxFeaturesEnum

    ExcelImportType(String humanReadableName, MDXFeaturesEnum mdxFeaturesEnum) {
        this.humanReadableName = humanReadableName
        this.mdxFeaturesEnum = mdxFeaturesEnum
    }

    private static final Map<String, ExcelImportType> humanReadableNameToEnum = new HashMap()
    static {
        for (ExcelImportType eIT: values()) {
            humanReadableNameToEnum.put(eIT.humanReadableName, eIT)
        }
    }

    static ExcelImportType fromHumanReadableName(String humanReadableName) {
        return humanReadableNameToEnum.get(humanReadableName)
    }


}

